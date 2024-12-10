package br.com.auto.bot.auth.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressoNivelDTO {
    private Integer nivelAtual;
    private double progressoGeral;
    private double progressoInvestimentos;
    private double progressoConvites;
    private double progressoDepositos;

    private Integer quantidadeInvestimentosAtual;
    private Integer quantidadeConvitesAtual;
    private BigDecimal totalDepositadoAtual;
}