package br.com.auto.bot.auth.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ProgressoNiveisDTO {
    private Integer nivelAtual;
    private Map<Integer, NivelRequirementProgressDTO> progressoPorNivel;
}