package br.com.auto.bot.auth.dto.filtro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltroLoginDTO {

    private String nome;

    private String perfil;

    private String sitema;

    private Long idSistema;
}
