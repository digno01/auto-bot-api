package br.com.auto.bot.auth.dto;

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
public class InvestimentoResponseDTO {
    private Long id;
    private String nomeRobo;
    private BigDecimal valorInicial;
    private BigDecimal saldoAtual;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "America/Sao_Paulo")
    private LocalDateTime dataInvestimento;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "America/Sao_Paulo")
    private LocalDateTime dataLiberacaoSaque;
    private String status;

    public static InvestimentoResponseDTO fromEntity(Investimento investimento) {
        return new InvestimentoResponseDTO(
                investimento.getId(),
                investimento.getRoboInvestidor().getNome(),
                investimento.getValorInicial(),
                investimento.getSaldoAtual(),
                investimento.getDataInvestimento(),
                investimento.getDataLiberacao(),
                investimento.getStatus().getDescricao()
        );
    }
}
