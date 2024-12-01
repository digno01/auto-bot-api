package br.com.auto.bot.auth.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
//    @JsonProperty("split_to")
    private String splitTo;
//    @JsonProperty("percent_split")
    private Integer percentSplit;
    @JsonProperty("postback_url")
    private String postbackUrl;
}
