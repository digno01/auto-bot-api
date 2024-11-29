package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.PaymentRequestDTO;
import br.com.auto.bot.auth.dto.WithdrawalRequestDTO;
import br.com.auto.bot.auth.exceptions.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class PaymentGatewayService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String publicToken;
    private final String secretToken;

    public PaymentGatewayService(
            RestTemplate restTemplate,
            @Value("${payment.api.baseUrl}") String baseUrl,
            @Value("${payment.api.publicToken}") String publicToken,
            @Value("${payment.api.secretToken}") String secretToken) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.publicToken = publicToken;
        this.secretToken = secretToken;
    }

    public ResponseEntity<Map> generatePaymentQRCode(PaymentRequestDTO request) {
        HttpHeaders headers = createHeaders();
        HttpEntity<PaymentRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            return restTemplate.exchange(
                    baseUrl + "/transactions.php",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
        } catch (RestClientException e) {
            log.error("Error generating QR Code", e);
            throw new PaymentException("Error generating QR Code", e);
        }
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
        headers.set("accept", "application/json");
        headers.set("content-type", "application/json");
        return headers;
    }
}
