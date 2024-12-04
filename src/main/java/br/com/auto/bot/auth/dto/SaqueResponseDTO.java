package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.model.Saque;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaqueResponseDTO {
    private Long id;
    private String nomeUsuario;
    private String nomeRobo;
    private BigDecimal valorSolicitado;
    private String statusSaque;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataProcessamento;

    public static SaqueResponseDTO fromEntity(Saque saque) {
        return new SaqueResponseDTO(
                saque.getId(),
                saque.getUsuario().getNome(),
                saque.getInvestimento().getRoboInvestidor().getNome(),
                saque.getValorSaque(),
                saque.getStatus().getDescricao(),
                saque.getDataSolicitacao(),
                saque.getDataProcessamento()
        );
    }
}
