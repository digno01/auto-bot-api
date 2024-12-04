package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessamentoSaqueRequest {
    private List<Long> idsSaques;
    private boolean aprovado;
}