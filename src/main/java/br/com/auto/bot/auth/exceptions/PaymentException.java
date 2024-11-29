package br.com.auto.bot.auth.exceptions;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final String errorCode;
    private final transient Object details;

    // Construtor básico
    public PaymentException(String message) {
        super(message);
        this.errorCode = "PAYMENT_ERROR";
        this.details = null;
    }

    // Construtor com causa
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PAYMENT_ERROR";
        this.details = null;
    }

    // Construtor com código de erro
    public PaymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    // Construtor completo
    public PaymentException(String message, String errorCode, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    // Construtor completo com causa
    public PaymentException(String message, String errorCode, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    // Métodos factory para criar exceções específicas
    public static PaymentException invalidAmount(String message) {
        return new PaymentException(message, "INVALID_AMOUNT");
    }

    public static PaymentException qrCodeGenerationError(String message, Throwable cause) {
        return new PaymentException(message, "QRCODE_GENERATION_ERROR", null, cause);
    }

    public static PaymentException withdrawalError(String message, Object details) {
        return new PaymentException(message, "WITHDRAWAL_ERROR", details);
    }

    public static PaymentException apiCommunicationError(String message, Throwable cause) {
        return new PaymentException(message, "API_COMMUNICATION_ERROR", null, cause);
    }
}
