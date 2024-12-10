package br.com.auto.bot.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FeriadoResponse {
    private LocalDate date;
    private boolean isFeriado;
    private String nomeFeriado;
}