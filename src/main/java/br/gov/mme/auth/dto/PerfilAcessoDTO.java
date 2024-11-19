package br.gov.mme.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfilAcessoDTO {

    private Long id;

    private String perfil;

    private String descricao;
}
