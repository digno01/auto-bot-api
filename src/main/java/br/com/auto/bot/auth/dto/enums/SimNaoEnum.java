package br.com.auto.bot.auth.dto.enums;


public enum SimNaoEnum {
    SIM("S", "Sim"),
    NAO("N", "Não");
    private String codigo;
    private String descicao;

    private SimNaoEnum(String codigo, String descicao) {
        this.codigo = codigo;
        this.descicao = descicao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescicao() {
        return descicao;
    }
}

