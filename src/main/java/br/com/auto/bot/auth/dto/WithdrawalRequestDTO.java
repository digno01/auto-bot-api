package br.com.auto.bot.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequestDTO {

    @JsonProperty("receiver_name")
    private String receiverName;
    @JsonProperty("value_cents")
    private BigDecimal valueCents;
    @JsonProperty("receiver_document")
    private String receiverDocument;
    @JsonProperty("pix_key")
    private String pixKey;
}