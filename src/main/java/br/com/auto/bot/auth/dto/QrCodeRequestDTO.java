package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class QrCodeRequestDTO {
    private BigDecimal amount;
    private Long idRobo;
}
