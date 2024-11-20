package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestimentoRequestDTO {
    private Long usuarioId;
    private Long roboId;
    private BigDecimal valorInvestimento;
    private String tipoOperacao; // "NOVO" ou "TROCA"
}
