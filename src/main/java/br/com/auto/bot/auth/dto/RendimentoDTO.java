package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendimentoDTO {
    private BigDecimal valorRendimento;
    private LocalDateTime dataRendimento;
    private String tipoRendimento;
    private BigDecimal percentualRendimento;
    private String nomeRobo;
    private BigDecimal valorInvestido;
    private Integer diasPeriodo;
    private BigDecimal rendimentoMin;
    private BigDecimal rendimentoMax;
    private Boolean isLucro;  // true para lucro, false para perda
}
