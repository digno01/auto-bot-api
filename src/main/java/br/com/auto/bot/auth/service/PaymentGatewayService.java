package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.*;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.exceptions.PaymentException;
import br.com.auto.bot.auth.exceptions.RegistroNaoEncontradoException;
import br.com.auto.bot.auth.model.Saque;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class PaymentGatewayService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String publicToken;
    private final String secretToken;
    private final String callBackPix;
    private final Integer percentualDeposito;
    private final String emailContratoGatway;
    private final InvestimentoService investimentoService;
    private final SaqueService saqueService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public PaymentGatewayService(
            RestTemplate restTemplate,
            @Value("${payment.api.baseUrl}") String baseUrl,
            @Value("${payment.api.publicToken}") String publicToken,
            @Value("${payment.api.secretToken}") String secretToken,
            @Value("${payment.api.callBackPix}") String callBackPix,
            @Value("${payment.api.emailContratoGatway}") String emailContratoGatway,
            @Value("${payment.api.percentualDeposito}") Integer percentualDeposito,
            UserService userService,
            SaqueService saqueService,
            InvestimentoService investimentoService) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.publicToken = publicToken;
        this.secretToken = secretToken;
        this.investimentoService = investimentoService;
        this.userService = userService;
        this.callBackPix = callBackPix;
        this.percentualDeposito = percentualDeposito;
        this.emailContratoGatway = emailContratoGatway;
        this.saqueService = saqueService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QrCodeResponseDTO generatePaymentQRCode(QrCodeRequestDTO qrCodeRequestDTO) throws RegistroNaoEncontradoException {
        //mock qrcode
       /* try {
        PaymentResponseDTO  responseBody = objectMapper.readValue("{ \"status\": \"success\",\"transaction\": {\"id\": \"35651\",\"pix_image_base64\": \"https://generator.qrcodefacil.com/qrcodes/static-5effce47e5dab0c07afd0a1570453e47.svg\",\"pix_payload\":\"00020126850014br.gov.bcb.pix2563pix.voluti.com.br/qr/v3/at/2c9628ca-8cc0-4f89-aacc-8692e6a200d75204000053039865802BR5909DET_PAY_62070503***630413B9\"  }}", PaymentResponseDTO.class);

        if (responseBody != null && "success".equals(responseBody.getStatus())) {
            String transactionId = responseBody.getTransaction().getId();
            QrCodeResponseDTO retorno = new QrCodeResponseDTO();
            retorno.setUrlQrCode(responseBody.getTransaction().getPix_image_base64());
            retorno.setIdTransacao(transactionId);
            retorno.setPixPayload("00020126850014br.gov.bcb.pix2563pix.voluti.com.br/qr/v3/at/2c9628ca-8cc0-4f89-aacc-8692e6a200d75204000053039865802BR5909LOYDS_PAY_62070503***630413B9");
            return  retorno;
        }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        investimentoService.permiteUsuarioInvestir(qrCodeRequestDTO);
        HttpHeaders headers = createHeaders();

        PaymentRequestDTO request = new PaymentRequestDTO();
        Customer custumer = new Customer();
        Document document =  new Document();
        Item item = new Item();
        Optional<User> optUser = userService.findByIdWithOutContacts(ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId());
        item.setUnitPrice(qrCodeRequestDTO.getAmount().multiply(new BigDecimal("100")));
        request.getItems().add(item);
        System.out.println(this.callBackPix);
        request.setPostbackUrl(this.callBackPix);
        request.setPercentSplit(this.percentualDeposito);
        request.setSplitTo(this.emailContratoGatway);
        User user = optUser.get();
       // user.getContato().get(0).setUser(null);
        document.setType("cpf");
        document.setNumber(user.getCpf());
        custumer.setDocument(document);
        custumer.setName(user.getNome());
        custumer.setEmail(user.getEmail());
        custumer.setPhone(user.getContato().get(0).getDdd().toString() +  user.getContato().get(0).getNumero().toString());
        request.setCustomer(custumer);

        request.setAmount(qrCodeRequestDTO.getAmount().multiply(new BigDecimal("100")));
        request.setPaymentMethod("pix");
        // Criar entity com headers e body
        HttpEntity<PaymentRequestDTO> entity = new HttpEntity<>(request, headers);
        try {
            System.out.println("Request body: " + objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // Fazer a requisição
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/transactions.php",
                HttpMethod.POST,
                entity,
                String.class
        );
        try {
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                PaymentResponseDTO responseBody =  objectMapper.readValue(response.getBody(), PaymentResponseDTO.class);
                if (responseBody != null && "success".equals(responseBody.getStatus())) {
                    String transactionId = responseBody.getTransaction().getId();
                    investValorQrCode(qrCodeRequestDTO, new BigDecimal(transactionId), responseBody.getTransaction().getPix_image_base64());
                    QrCodeResponseDTO retorno = new QrCodeResponseDTO();
                    retorno.setUrlQrCode(responseBody.getTransaction().getPix_image_base64());
                    retorno.setIdTransacao(transactionId);
                    retorno.setPixPayload(responseBody.getTransaction().getPix_payload());
                    return  retorno;
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro no processamento do QRCode", e);
        }
        return null;
    }

    private void investValorQrCode(QrCodeRequestDTO qrCodeRequestDTO, BigDecimal idTransacaoPagamentoGatway, String qrCode) {
        InvestimentoRequestDTO investimento = new InvestimentoRequestDTO();
        investimento.setUsuarioId(ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId());
        investimento.setValorInvestimento(qrCodeRequestDTO.getAmount());
        investimento.setRoboId(qrCodeRequestDTO.getIdRobo());
        investimento.setIdTransacaoPagamentoGateway(idTransacaoPagamentoGatway);
        investimento.setUrlQrcode(qrCode);
        investimentoService.processarInvestimento(investimento);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Map> makeWithdrawal(SaqueRequestDTO request) {
        try {
            HttpHeaders headers = createHeaders();
            WithdrawalRequestDTO saqueRequestGateway = new WithdrawalRequestDTO();
            // Verifica se o valor de saqueRequestGateway é positivo
            if (request.getValorSaque() == null) {
                throw new BusinessException("Informe o valor de saqueRequestGateway.");
            } else if(request.getValorSaque().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException("O valor do saqueRequestGateway deve ser positivo.");
            }
            BigDecimal saldoDisponivelSaque = investimentoService.recuperaSaldoDisponivelSaqueInvestimento(request.getInvestimentoId());
            // Verifica se o valor do saqueRequestGateway é menor ou igual ao saldo disponível
            if (request.getValorSaque().compareTo(saldoDisponivelSaque) > 0) {
                throw new BusinessException("O valor do saqueRequestGateway não pode ser maior que o saldo disponível.");
            }

            Saque saque =  saqueService.perepararSaque(request, ObterDadosUsuarioLogado.getUsuarioLogadoId());
            Optional<User> optuser = userService.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId());
            User user = optuser.get();

            saqueRequestGateway.setValueCents(request.getValorSaque().multiply(new BigDecimal("100")).intValue());
            saqueRequestGateway.setReceiverName(user.getNome());
            saqueRequestGateway.setReceiverDocument("CPF");
            saqueRequestGateway.setPixKey(user.getCpf());

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
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                if(response.getBody().contains("\"status\":\"error\"")){
                    throw new BusinessException("Saldo insuficiente");
                }
                PaymentResponseDTO responseBody =  objectMapper.readValue(response.getBody(), PaymentResponseDTO.class);
                if (responseBody != null && "success".equals(responseBody.getStatus())) {
                    saque.setIdSaqueGateway(responseBody.getResponse().getId());
                    saque.setEndToEndId(responseBody.getResponse().getEndToEndId());
                    saqueService.save(saque);
                }else{
                    throw new BusinessException("Saldo insuficiente");
                }
            }
            return null;

        } catch (HttpServerErrorException e) {e.printStackTrace();
            System.err.println("Server error: " + e.getResponseBodyAsString());
            throw new BusinessException("Erro no servidor ao processar saque: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing withdrawal: " + e.getMessage());
            throw new BusinessException("Erro ao processar saque: " + e.getMessage());
        }
    }

//        try {
//            return restTemplate.exchange(
//                    baseUrl + "/saque.php",
//                    HttpMethod.POST,
//                    entity,
//                    Map.class
//            );
//
//        } catch (RestClientException e) {
//            log.error("Error processing withdrawal", e);
//            throw new PaymentException("Error processing withdrawal", e);
//        }
//    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("public_token", publicToken);
        headers.set("secret_token", secretToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public void processarPagamento(PaymentCallBackDTO pagamento) {
        investimentoService.processarPagamentoInvestimento(pagamento);
    }
}
