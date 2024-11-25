package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.model.Investimento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestimentoResponseDTO {
    private Long id;
    private String nomeRobo;
    private BigDecimal valorInicial;
    private BigDecimal saldoAtual;
    private LocalDateTime dataInvestimento;
    private LocalDateTime dataLiberacaoSaque;
    private String status;

    public static InvestimentoResponseDTO fromEntity(Investimento investimento) {
        return new InvestimentoResponseDTO(
                investimento.getId(),
                investimento.getRoboInvestidor().getNome(),
                investimento.getValorInicial(),
                investimento.getSaldoAtual(),
                investimento.getDataInvestimento(),
                investimento.getDataLiberacao().plusDays(
                        investimento.getRoboInvestidor().getDiasPeriodo()
                ),
                investimento.getStatus().getDescricao()
        );
    }
}
