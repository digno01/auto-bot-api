package br.com.auto.bot.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class IndicadoDTO {
    private String nome;
    private Integer nivelConta;
    private BigDecimal totalInvestido;
    private Integer quantidadeInvestimentos;
    private BigDecimal lucroTotal;
    private LocalDateTime primeiroDeposito;
    private BigDecimal comissao;
}