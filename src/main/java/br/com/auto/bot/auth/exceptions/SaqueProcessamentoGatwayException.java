package br.com.auto.bot.auth.exceptions;

public class SaqueProcessamentoGatwayException extends RuntimeException {
    public SaqueProcessamentoGatwayException(String message) {
        super(message);
    }

    public SaqueProcessamentoGatwayException(String message, Throwable cause) {
        super(message, cause);
    }
}
