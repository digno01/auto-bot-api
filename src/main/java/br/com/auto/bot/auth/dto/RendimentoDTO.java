package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.enums.TipoResultado;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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
    private BigDecimal valorAcumulado;
}
