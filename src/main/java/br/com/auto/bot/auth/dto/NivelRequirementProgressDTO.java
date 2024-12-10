package br.com.auto.bot.auth.dto;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class NivelRequirementProgressDTO {
    private Integer investimentosAtuais;
    private Integer investimentosNecessarios;
    private Double percentualInvestimentos;

    private Integer convitesAtuais;
    private Integer convitesNecessarios;
    private Double percentualConvites;

    private BigDecimal depositoAtual;
    private BigDecimal depositoNecessario;
    private Double percentualDeposito;

    private Double percentualTotal;
    private Boolean nivelAtingido;
}