package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InvestimentoSaqueDTO {
    private Long investimentoId;
    private String nomeRobo;
    private LocalDateTime dataInvestimento;
    private LocalDateTime dataLiberacao;
    private BigDecimal valorInicial;
    private BigDecimal saldoAtual;
}
