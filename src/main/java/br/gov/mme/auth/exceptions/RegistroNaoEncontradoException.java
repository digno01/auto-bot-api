package br.gov.mme.auth.exceptions;

public class RegistroNaoEncontradoException extends Throwable {
    public RegistroNaoEncontradoException(String msg){
        super(msg);
    }

    public RegistroNaoEncontradoException(String message, Throwable cause) {

        super(message, cause);
    }
}
