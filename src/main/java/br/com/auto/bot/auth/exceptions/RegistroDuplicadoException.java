package br.com.auto.bot.auth.exceptions;

public class RegistroDuplicadoException extends RuntimeException {
    public RegistroDuplicadoException(String msg){
        super(msg);
    }

    public RegistroDuplicadoException(String message, Throwable cause) {

        super(message, cause);
    }
}
