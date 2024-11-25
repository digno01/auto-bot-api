package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaqueRequestDTO {
    private Long investimentoId;
    private BigDecimal valorSaque;
}
