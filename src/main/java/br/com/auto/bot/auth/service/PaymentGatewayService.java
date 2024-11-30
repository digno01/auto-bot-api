package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.*;
import br.com.auto.bot.auth.exceptions.PaymentException;
import br.com.auto.bot.auth.exceptions.RegistroNaoEncontradoException;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.service.feign.PaymentClient;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PaymentClient paymentClient;

    public PaymentGatewayService(
            RestTemplate restTemplate,
            @Value("${payment.api.baseUrl}") String baseUrl,
            @Value("${payment.api.publicToken}") String publicToken,
            @Value("${payment.api.secretToken}") String secretToken,
            @Value("${payment.api.callBackPix}") String callBackPix,
            @Value("${payment.api.emailContratoGatway}") String emailContratoGatway,
            @Value("${payment.api.percentualDeposito}") Integer percentualDeposito,
            UserService userService,
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
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QrCodeResponseDTO generatePaymentQRCode(QrCodeRequestDTO qrCodeRequestDTO) throws RegistroNaoEncontradoException {
        investimentoService.permiteUsuarioInvestir(qrCodeRequestDTO);
        HttpHeaders headers = createHeaders();

        PaymentRequestDTO request = new PaymentRequestDTO();
        Customer custumer = new Customer();
        Document document =  new Document();
        Item item = new Item();
        Optional<User> optUser = userService.findByIdWithOutContacts(ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId());
        item.setUnitPrice(qrCodeRequestDTO.getAmount());
        request.getItems().add(item);
        request.setPostbackUrl(this.callBackPix);
        request.setPercentSplit(this.percentualDeposito);
        request.setSplitTo(this.emailContratoGatway);
        User user = optUser.get();
        user.getContato().get(0).setUser(null);
        document.setType("cpf");
        document.setNumber(user.getCpf());
        custumer.setDocument(document);
        custumer.setName(user.getNome());
        custumer.setEmail(user.getEmail());
        custumer.setPhone(user.getContato().get(0).getDdd().toString() +  user.getContato().get(0).getNumero().toString());
        request.setCustomer(custumer);

        request.setAmount(qrCodeRequestDTO.getAmount());
        request.setPaymentMethod("pix");
        // Aqui estamos usando Jackson para converter o objeto request em JSON
        try {

            String jsonRequest = objectMapper.writeValueAsString(request);
            System.out.println("JSON Request: " + jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Criar entity com headers e body
        HttpEntity<PaymentRequestDTO> entity = new HttpEntity<>(request, headers);

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
                    // Processar a transação e o ID da transação
                    String transactionId = responseBody.getTransaction().getId();
                    investValorQrCode(qrCodeRequestDTO, new BigDecimal(transactionId));
                    QrCodeResponseDTO retorno = new QrCodeResponseDTO();
                    retorno.setUrlQrCode(responseBody.getTransaction().getPix_image_base64());
                    retorno.setIdTransacao(transactionId);
                    return  retorno;

                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar resposta JSON", e);
        }
        return null;
    }

    private void investValorQrCode(QrCodeRequestDTO qrCodeRequestDTO, BigDecimal idTransacaoPagamentoGatway) {
        InvestimentoRequestDTO investimento = new InvestimentoRequestDTO();
        investimento.setUsuarioId(ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId());
        investimento.setValorInvestimento(qrCodeRequestDTO.getAmount());
        investimento.setRoboId(qrCodeRequestDTO.getIdRobo());
        investimento.setIdTransacaoPagamentoGateway(idTransacaoPagamentoGatway);
        investimentoService.processarInvestimento(investimento);
    }

    public ResponseEntity<Map> makeWithdrawal(WithdrawalRequestDTO request) {
        HttpHeaders headers = createHeaders();
        HttpEntity<WithdrawalRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            return restTemplate.exchange(
                    baseUrl + "/saque.php",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
        } catch (RestClientException e) {
            log.error("Error processing withdrawal", e);
            throw new PaymentException("Error processing withdrawal", e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("public_token", publicToken);
        headers.set("secret_token", secretToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.set("accept", "application/json");
//        headers.set("content-type", "application/json");
        return headers;
    }
}
