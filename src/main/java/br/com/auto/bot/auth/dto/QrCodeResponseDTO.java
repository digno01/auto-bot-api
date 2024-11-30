package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QrCodeResponseDTO {
    private String idTransacao;
    private String urlQrCode;
}
