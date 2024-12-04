package br.com.auto.bot.auth.enums;

import lombok.Getter;

@Getter
public enum TipoNotificacao {
    INVESTIMENTO_SOLICITADO("Investimento Solicitado"),
    INVESTIMENTO_PAGO("Investimento Pago"),
    INVESTIMENTO_PAGO_PARCIAL("Investimento Pago Parcial"),
    INVESTIMENTO_CANCELADO("Investimento Cancelado"),
    INVESTIMENTO_REINVESTIDO("Investimento Reinvestido"),
    INVESTIMENTO_FINALIZADO("Investimento Finalizado"),
    SAQUE_SOLICITADO("Saque Solicitado"),
    SAQUE_APROVADO("Saque Aprovado"),
    SAQUE_REPROVADO("Saque Reprovado"),
    COMISSAO_RECEBIDA("Comissão Recebida"),
    MENSAGEM_SISTEMA("Mensagem do Sistema");

    private final String descricao;

    TipoNotificacao(String descricao) {
        this.descricao = descricao;
    }
}
