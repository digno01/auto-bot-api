package br.com.auto.bot.auth.enums;


public enum StatusInvestimento {
    A("ATIVO"),
    P("PENDENTE"),
    F("FINALIZADO"),
    C("CANCELADO");

    private String descricao;

    StatusInvestimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
