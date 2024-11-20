package br.com.auto.bot.auth.enums;

public enum StatusDeposito {
    P("PENDENTE"),
    A("APROVADO"),
    R("REJEITADO"),
    C("CANCELADO");

    private String descricao;

    StatusDeposito(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
