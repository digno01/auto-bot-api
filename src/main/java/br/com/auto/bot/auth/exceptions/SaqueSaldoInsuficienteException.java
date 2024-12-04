package br.com.auto.bot.auth.exceptions;

public class SaqueSaldoInsuficienteException extends RuntimeException {
    public SaqueSaldoInsuficienteException(String message) {
        super(message);
    }

    public SaqueSaldoInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
