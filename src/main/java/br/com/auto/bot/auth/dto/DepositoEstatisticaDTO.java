package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositoEstatisticaDTO {
    private Long quantidade;
    private BigDecimal valorTotal;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;
    private BigDecimal valorMedio;
}