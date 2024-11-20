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
    private BigDecimal valorInvestido;
    private BigDecimal percentualRendimentoDiario;
    private LocalDateTime dataInicio;
    private LocalDateTime dataLiberacaoSaque;
    private String status;
    private BigDecimal saldoInvestido;
    private BigDecimal saldoRendimentos;

    public static InvestimentoResponseDTO fromEntity(Investimento investimento) {
        return new InvestimentoResponseDTO(
                investimento.getId(),
                investimento.getRoboInvestidor().getNome(),
                investimento.getValorInvestido(),
                investimento.getPercentualRendimentoDiario(),
                investimento.getDataInicio(),
                investimento.getDataInicio().plusDays(
                        investimento.getRoboInvestidor().getDiasPeriodo()
                ),
                investimento.getStatus().getDescricao(),
                investimento.getUsuario().getSaldoInvestido(),
                investimento.getUsuario().getSaldoRendimentos()
        );
    }

    // Construtor adicional sem os saldos (para compatibilidade)
    public InvestimentoResponseDTO(
            Long id,
            String nomeRobo,
            BigDecimal valorInvestido,
            BigDecimal percentualRendimentoDiario,
            LocalDateTime dataInicio,
            LocalDateTime dataLiberacaoSaque,
            String status) {
        this.id = id;
        this.nomeRobo = nomeRobo;
        this.valorInvestido = valorInvestido;
        this.percentualRendimentoDiario = percentualRendimentoDiario;
        this.dataInicio = dataInicio;
        this.dataLiberacaoSaque = dataLiberacaoSaque;
        this.status = status;
    }
}
