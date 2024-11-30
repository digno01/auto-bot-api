package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Serializable {
    private Boolean tangible = Boolean.TRUE;
    private String title = "Saldo";
    private BigDecimal unitPrice;
    private Integer quantity = 1;
}