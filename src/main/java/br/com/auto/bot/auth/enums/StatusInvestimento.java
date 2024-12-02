package br.com.auto.bot.auth.enums;


public enum StatusInvestimento {
    A("Ativo"),
    P("Pendente Pagamento"),
    PP("Pagamento Parcial"),
    SL("Saque Solicitado"),
    R("Reinvestido"),
    F("Finalizado"),
    C("Cancelado");

    private String descricao;

    StatusInvestimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
