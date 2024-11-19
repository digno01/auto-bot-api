package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgaoInstituicaoDTO {
    private String sigla;
    private String descricao;
}
