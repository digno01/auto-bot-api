package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.PaymentRequestDTO;
import br.com.auto.bot.auth.dto.WithdrawalRequestDTO;
import br.com.auto.bot.auth.service.PaymentGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// PaymentController.java
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class PaymentController {
    private final PaymentGatewayService paymentGatewayService;

    public PaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/qrcode")
    public ResponseEntity<Map> generateQRCode(@RequestBody PaymentRequestDTO request) {
        return paymentGatewayService.generatePaymentQRCode(request);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<Map> processWithdrawal(@RequestBody WithdrawalRequestDTO request) {
        return paymentGatewayService.makeWithdrawal(request);
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody Map<String, Object> callback) {
        // Processar o callback
        // Implementar lógica de processamento do callback
        return ResponseEntity.ok().build();
    }
}
