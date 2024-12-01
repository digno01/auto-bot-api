package br.com.auto.bot.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallBackDTO {
    private String id;
    private int status;
    @JsonProperty("payment_method")
    private String paymentMethod;
    private String amount;
    @JsonProperty("external_ref")
    private String externalRef;
}
