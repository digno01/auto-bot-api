package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestimentoResumoDTO {
    private String nomeRobo;
    private Long quantidadeInvestimentos;
    private BigDecimal valorTotalInvestido;
    private BigDecimal mediaPercentualRendimento;
}
