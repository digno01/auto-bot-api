package br.com.auto.bot.auth.enums;

public enum TipoRendimento {
    I("I", "INVESTIMENTO"),
    L("L", "LOSS_TOTAL"),
    N1("1", "NIVEL_1"),
    N2("2", "NIVEL_2"),
    N3("3", "NIVEL_3");

    private String codigo;
    private String descricao;

    TipoRendimento(String codigo, String descricao) {
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
