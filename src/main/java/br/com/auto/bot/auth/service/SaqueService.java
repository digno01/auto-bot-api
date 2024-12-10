package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.*;
import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.enums.StatusSaque;
import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.exceptions.SaqueProcessamentoGatwayException;
import br.com.auto.bot.auth.exceptions.SaqueSaldoInsuficienteException;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.Saque;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.InvestimentoRepository;
import br.com.auto.bot.auth.repository.SaqueRepository;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SaqueService {

    @Autowired
    private InvestimentoRepository investimentoRepository;
    @Autowired
    private NotificacaoUsuarioService notificacaoService;
    @Lazy
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    private final ObjectMapper objectMapper;
    @Autowired
    private InvestimentoService investimentoService;

    @Autowired
    private SaqueRepository saqueRepository;
    @Autowired
    private NotificacaoUsuarioService notificacaoUsuarioService;
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String publicToken;
    private final String secretToken;
    private final String callBackPix;
    private final Integer percentualDeposito;
    private final String emailContratoGatway;
    public SaqueService(
    RestTemplate restTemplate,
    @Value("${payment.api.baseUrl}") String baseUrl,
    @Value("${payment.api.publicToken}") String publicToken,
    @Value("${payment.api.secretToken}") String secretToken,
    @Value("${payment.api.callBackPix}") String callBackPix,
    @Value("${payment.api.emailContratoGatway}") String emailContratoGatway,
    @Value("${payment.api.percentualDeposito}") Integer percentualDeposito){

        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.publicToken = publicToken;
        this.secretToken = secretToken;
        this.callBackPix = callBackPix;
        this.percentualDeposito = percentualDeposito;
        this.emailContratoGatway = emailContratoGatway;

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Saque perepararSaque(SaqueRequestDTO request, Long usuarioId) {

        Investimento investimento =  investimentoRepository.findInvestimentoAtivoByUsuarioId(request.getInvestimentoId(), ObterDadosUsuarioLogado.getUsuarioLogadoId());
        if(investimento == null){
            throw new BusinessException("Investimento não encontrado");
        }

        // Verificando se o saldo disponível é suficiente
        if (request.getValorSaque().compareTo(investimento.getSaldoAtual()) > 0) {
            throw new BusinessException("Saldo insuficiente para realizar o saque.");
        }
        try{

            // Criando a solicitação de saque
            Saque saque = new Saque();
            saque.setUsuario(investimento.getUsuario());
            saque.setInvestimento(investimento);
            saque.setValorSaque(request.getValorSaque());
            saque.setStatus(StatusSaque.P); // Status Pendente
            saqueRepository.save(saque);
            investimento.setStatus(StatusInvestimento.SL);
            investimentoRepository.save(investimento);

            enviarNotificacao(investimento.getUsuario(), "Saque Solicitado", "Solicitado saque no valor de  R$ " + saque.getValorSaque() + " para o investimento no " + investimento.getRoboInvestidor().getNome(), saque.getValorSaque(), TipoNotificacao.SAQUE_SOLICITADO);
            return saque;
        }catch (Exception e ){
            throw new BusinessException("Erro ao processar saque.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Saque save(Saque saque) {
        return saqueRepository.save(saque);
    }

    public List<SaqueResponseDTO> listarSolicitacoesSaque(Long usuarioId) {
        List<Saque> saques = saqueRepository.findByUsuarioId(usuarioId);

        return saques.stream()
                .map(saque -> new SaqueResponseDTO(
                        saque.getId(),
                        saque.getUsuario().getNome(),
                        saque.getInvestimento().getRoboInvestidor().getNome(),
                        saque.getValorSaque(),
                        saque.getStatus().getDescricao(),
                        saque.getDataSolicitacao(),
                        saque.getDataProcessamento()
                ))
                .collect(Collectors.toList());
    }


    public Page<SaqueResponseDTO> findAllPendentes(Pageable pageable) {
        Page<Saque> saques = saqueRepository.findAllByStatus(StatusSaque.P, pageable);
        return saques.map(SaqueResponseDTO::fromEntity);
    }


    @Transactional
    public List<Saque> processarSaques(List<Long> idsSaques, boolean aprovado) {
        List<Saque> saques = saqueRepository.findAllById(idsSaques);

        if (saques.isEmpty()) {
            throw new BusinessException("Nenhum saque encontrado com os IDs fornecidos");
        }

        saques.forEach(saque -> {
            processarSaqueIndividual(aprovado, saque);
        });

        return saques;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processarSaqueIndividual(boolean aprovado, Saque saque) {
        try{
            saque.setDataProcessamento(LocalDateTime.now());
            saque.setStatus(aprovado ? StatusSaque.A : StatusSaque.R);
            if(aprovado){
                processarSaqueIndividual(saque);
                enviarNotificacao( saque.getUsuario(), "Saque Aprovado", "Seu saque R$ " + saque.getValorSaque() + " foi aprovado!", saque.getValorSaque(), TipoNotificacao.SAQUE_APROVADO);
            }else{
                saque.getInvestimento().setStatus(StatusInvestimento.A);
                investimentoRepository.save(saque.getInvestimento());
                enviarNotificacao( saque.getUsuario(), "Saque Reprovado", "Seu saque R$ " + saque.getValorSaque() + " foi reprovado!", saque.getValorSaque(), TipoNotificacao.SAQUE_REPROVADO);
            }
            saqueRepository.save(saque);
        }catch (SaqueSaldoInsuficienteException e){
            saque.setStatus(StatusSaque.C);
            saqueRepository.save(saque);
            enviarNotificacao(saque.getUsuario(),"Investimento sem saldo. ", "Saque cancelado investimento sem saldo.", saque.getValorSaque(), TipoNotificacao.MENSAGEM_SISTEMA);
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void processarSaqueIndividual(Saque saque) {
        try {
            HttpHeaders headers = createHeaders();
            // Verifica se o valor de saqueRequestGateway é positivo
            if (saque.getValorSaque() == null) {
                throw new BusinessException("Informe o valor de saqueRequestGateway.");
            } else if(saque.getValorSaque().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("O valor do saqueRequestGateway deve ser positivo.");
            }
            BigDecimal saldoDisponivelSaque = saque.getInvestimento().getSaldoAtual();
            // Verifica se o valor do saqueRequestGateway é menor ou igual ao saldo disponível
            if (saque.getValorSaque().compareTo(saldoDisponivelSaque) > 0) {
                throw new SaqueSaldoInsuficienteException("O valor do saqueRequestGateway não pode ser maior que o saldo disponível.");
            }
            User user = saque.getUsuario();
            WithdrawalRequestDTO saqueRequestGateway = new WithdrawalRequestDTO();

            // Calcula valor da taxa (7%)
            BigDecimal taxa = new BigDecimal("0.07");
            BigDecimal valorOriginal = saque.getValorSaque();
            BigDecimal valorTaxa = valorOriginal.multiply(taxa);
            BigDecimal valorFinal = valorOriginal.subtract(valorTaxa);

            // Converte para centavos e define no DTO
            saqueRequestGateway.setValueCents(valorFinal.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
            //saqueRequestGateway.setValueCents(saque.getValorSaque().multiply(new BigDecimal("100")).intValue());
            saqueRequestGateway.setReceiverName(user.getNome());
            saqueRequestGateway.setReceiverDocument("CPF");
            saqueRequestGateway.setPixKey(user.getCpf());

            addGatwaySaldoRendimento(user, saqueRequestGateway.getValueCents().negate());
            HttpEntity<WithdrawalRequestDTO> entity = new HttpEntity<>(saqueRequestGateway, headers);
            System.out.println("Request body: " + objectMapper.writeValueAsString(saqueRequestGateway));
            // Fazer a requisição
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/saque.php",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            System.out.println("Response body: " + objectMapper.writeValueAsString(response));
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                if(response.getBody().contains("\"status\":\"error\"")){
                    throw new SaqueSaldoInsuficienteException("Saldo insuficiente");
                }
                PaymentResponseDTO responseBody =  objectMapper.readValue(response.getBody(), PaymentResponseDTO.class);
                if (responseBody != null && "success".equals(responseBody.getStatus())) {
                    saque.setIdSaqueGateway(responseBody.getResponse().getId());
                    saque.setEndToEndId(responseBody.getResponse().getEndToEndId());
                    saque.setStatus(StatusSaque.A);
                    saqueRepository.save(saque);
                    Investimento investimento =   saque.getInvestimento();
                    investimento.setSaldoAtual(saldoDisponivelSaque.subtract(saque.getValorSaque()));
                    if(BigDecimal.ZERO.equals(investimento.getSaldoAtual())){
                        investimento.setStatus(StatusInvestimento.F);
                        enviarNotificacao( saque.getUsuario(), "Investimento Finalizado", "Seu investimento foi finalizado!", saque.getValorSaque(), TipoNotificacao.INVESTIMENTO_FINALIZADO);
                    }else{
                        investimento.setStatus(StatusInvestimento.A);
                        enviarNotificacao( saque.getUsuario(), "Investimento Ativo", "Seu investimento está ativo novamente!", saque.getValorSaque(), TipoNotificacao.MENSAGEM_SISTEMA);
                    }
                    investimentoService.salvar(investimento);
                }else{
                    throw new SaqueSaldoInsuficienteException("Saldo insuficiente");
                }
            }
        } catch (HttpServerErrorException e) {e.printStackTrace();
            System.err.println("Server error: " + e.getResponseBodyAsString());
            throw new SaqueProcessamentoGatwayException("Erro no servidor ao processar saque: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing withdrawal: " + e.getMessage());
            throw new SaqueProcessamentoGatwayException("Erro ao processar saque: " + e.getMessage());
        }
    }

    private void addGatwaySaldoRendimento(User usuario, BigDecimal rendimentoBruto) {
        QrCodeRequestDTO requestDTO = new QrCodeRequestDTO();
        requestDTO.setAmount(rendimentoBruto);
        paymentGatewayService.addRendimentoInvestidorGatway(requestDTO, usuario);
    }

    private void enviarNotificacao(User user, String titulo, String mensagem, BigDecimal valor, TipoNotificacao tipoNoficacao) {
        notificacaoService.criarNotificacao(
                user,
                titulo,
                mensagem,
                valor,
                tipoNoficacao
        );
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("public_token", publicToken);
        headers.set("secret_token", secretToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
