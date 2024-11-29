package br.com.auto.bot.auth.dto;

import lombok.Data;

@Data
public class WithdrawalRequestDTO {
    private Integer valueCents;
    private String receiverName;
    private String receiverDocument;
    private String pixKey;
}