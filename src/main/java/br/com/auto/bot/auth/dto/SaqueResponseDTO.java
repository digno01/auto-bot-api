package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaqueResponseDTO {
    private String nomeRobo;
    private BigDecimal valorSolicitado;
    private String statusSaque;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataProcessamento;

}
