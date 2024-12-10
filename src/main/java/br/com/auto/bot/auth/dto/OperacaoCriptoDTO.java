package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperacaoCriptoDTO {
    private Long id;
    private RendimentoDTO rendimento;
    private String moeda;  // BTC, ETH, etc.
    private BigDecimal valorCompra;
    private BigDecimal valorVenda;
    private BigDecimal quantidadeMoeda;
    private LocalDateTime dataCompra;
    private LocalDateTime dataVenda;
    private BigDecimal percentualVariacao;
    private BigDecimal valorLucro;
    private String urlImagem;
}
