package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.*;
import br.com.auto.bot.auth.exceptions.RegistroNaoEncontradoException;
import br.com.auto.bot.auth.service.PaymentGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// PaymentController.java
@RestController
@RequestMapping("/api/payments")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class PaymentController {
    private final PaymentGatewayService paymentGatewayService;

    public PaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/qrcode")
    public ResponseEntity<QrCodeResponseDTO> generateQRCode(@RequestBody QrCodeRequestDTO request) throws RegistroNaoEncontradoException {
        return ResponseEntity.ok(paymentGatewayService.generatePaymentQRCode(request));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<Map> processWithdrawal(@RequestBody SaqueDTO request) {
        return paymentGatewayService.makeWithdrawal(request);
    }

    @PostMapping("/detpay-callback")
    public ResponseEntity<Void> handleCallback(@RequestBody PaymentCallBackDTO pagamento) {
        pagamento.toString();
        return ResponseEntity.status(400).build();
    }
}
