package br.gov.mme.auth.exceptions.intefaces;

public interface MessageCode {

    /**
     * Retorna o código da mensagem disponível no *.properties de mensagem.
     *
     * @return
     */
    public String getCode();

    /**
     * Retorna o Status HTTP referente a mensagem.
     *
     * @return
     */
    public Integer getStatus();
}
