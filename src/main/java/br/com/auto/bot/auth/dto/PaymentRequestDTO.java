package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentRequestDTO implements Serializable {
    private Customer customer;
    private BigDecimal amount;
    private String paymentMethod;
    private List<Item> items = new ArrayList<>();
    private String splitTo;
    private Integer percentSplit;
    private String postbackUrl;
}
