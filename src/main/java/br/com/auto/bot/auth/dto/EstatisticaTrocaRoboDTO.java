package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class EstatisticaTrocaRoboDTO {
    private String nomeRobo;
    private Long quantidadeTrocas;
    private BigDecimal mediaValorTransferido;
    private BigDecimal mediaRendimentosIncorporados;
}
