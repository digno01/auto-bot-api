package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriptoData {
    private String id;
    private String symbol;
    private String name;
    private String image;
    private BigDecimal currentPrice;
    private BigDecimal priceChangePercentage24h;
    private BigDecimal low24h;
    private BigDecimal high24h;
}
