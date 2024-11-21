package br.com.auto.bot.auth.enums;

public enum TipoResultado {
    LUCRO("L", "LUCRO"),
    PERDA("P", "PERDA");

    private String codigo;
    private String descricao;

    TipoResultado(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
