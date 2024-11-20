package br.com.auto.bot.auth.enums;

public enum StatusSaque {
    P("PENDENTE"),
    A("APROVADO"),
    R("REJEITADO"),
    C("CANCELADO");

    private String descricao;

    StatusSaque(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
