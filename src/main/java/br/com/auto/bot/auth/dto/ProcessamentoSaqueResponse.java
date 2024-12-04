package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessamentoSaqueResponse {
    private int quantidadeProcessada;
    private String mensagem;
    private LocalDateTime dataProcessamento;
}