package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.enums.TipoResultado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendimentoDTO {
    private Long id;
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
    private TipoResultado tipoResultado;
}
