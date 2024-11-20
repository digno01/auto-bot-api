package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.model.Deposito;
import br.com.auto.bot.auth.model.Investimento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositoResponseDTO {
    private Long depositoId;
    private Long investimentoId;
    private BigDecimal valorDeposito;
    private String statusDeposito; // será a descrição do enum
    private String statusInvestimento;
    private LocalDateTime dataDeposito;
    private LocalDateTime dataLiberacaoSaque;
    private String nomeRobo;
    private BigDecimal saldoInvestido;
    private BigDecimal saldoRendimentos;

    public static DepositoResponseDTO fromEntities(Deposito deposito, Investimento investimento) {
        return new DepositoResponseDTO(
                deposito.getId(),
                investimento.getId(),
                deposito.getValorDeposito(),
                deposito.getStatus().getDescricao(),
                investimento.getStatus().getDescricao(),
                deposito.getDataDeposito(),
                investimento.getDataInicio().plusDays(
                        investimento.getRoboInvestidor().getDiasPeriodo()
                ),
                investimento.getRoboInvestidor().getNome(),
                investimento.getUsuario().getSaldoInvestido(),
                investimento.getUsuario().getSaldoRendimentos()
        );
    }
}
