package br.gov.mme.auth.exceptions;

public class RegistroDuplicadoException extends Throwable {
    public RegistroDuplicadoException(String msg){
        super(msg);
    }

    public RegistroDuplicadoException(String message, Throwable cause) {

        super(message, cause);
    }
}
