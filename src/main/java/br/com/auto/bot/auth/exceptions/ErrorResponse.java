package br.com.auto.bot.auth.exceptions;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;
    private Object details;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(int status, String errorCode, String message) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(int status, String errorCode, String message, Object details) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }
}
