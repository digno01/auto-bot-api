package br.com.auto.bot.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseDTO implements Serializable {
    private String status;
    private String message;
    private Transaction transaction;
    private SaqueGatewayResponseDTO response;
}
