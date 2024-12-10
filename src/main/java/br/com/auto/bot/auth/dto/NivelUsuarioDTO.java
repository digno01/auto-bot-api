package br.com.auto.bot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelUsuarioDTO {

    private Integer nivelAtual;
    private Integer percentualNivel1;
    private Integer percentualNivel2;
    private Integer percentualNivel3;
    private Integer percentualNivel4;
    private Integer percentualNivel5;
    private Integer percentualComissao;
    private Integer numeroConvites;
    private BigDecimal depositoTotal;
    private Integer qtdInvestimento;
}
