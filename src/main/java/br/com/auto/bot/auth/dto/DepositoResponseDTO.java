package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.model.Deposito;
import br.com.auto.bot.auth.model.Investimento;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataDeposito;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataLiberacaoSaque;
    private String nomeRobo;

    public static DepositoResponseDTO fromEntities(Deposito deposito, Investimento investimento) {
        return new DepositoResponseDTO(
                deposito.getId(),
                investimento.getId(),
                deposito.getValorDeposito(),
                deposito.getStatus().getDescricao(),
                investimento.getStatus().getDescricao(),
                deposito.getDataDeposito(),
                investimento.getDataInvestimento().plusDays(
                        investimento.getRoboInvestidor().getDiasPeriodo()
                ),
                investimento.getRoboInvestidor().getNome()
        );
    }
}
