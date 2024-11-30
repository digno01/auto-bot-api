package br.com.auto.bot.auth.dto;

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
    private String paymentMethod;
    private String amount;
    private String externalRef;
}
