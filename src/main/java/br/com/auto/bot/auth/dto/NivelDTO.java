package br.com.auto.bot.auth.dto;

import java.math.BigDecimal;

public class NivelDTO {
    private Integer nivel;
    private Integer investimentosMinimos;
    private Integer convitesMinimos;
    private BigDecimal depositoMinimo;

    public NivelDTO(Integer nivel, Integer investimentosMinimos, Integer convitesMinimos, BigDecimal depositoMinimo) {
        this.nivel = nivel;
        this.investimentosMinimos = investimentosMinimos;
        this.convitesMinimos = convitesMinimos;
        this.depositoMinimo = depositoMinimo;
    }

    // Getters e Setters
    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getInvestimentosMinimos() {
        return investimentosMinimos;
    }

    public void setInvestimentosMinimos(Integer investimentosMinimos) {
        this.investimentosMinimos = investimentosMinimos;
    }

    public Integer getConvitesMinimos() {
        return convitesMinimos;
    }

    public void setConvitesMinimos(Integer convitesMinimos) {
        this.convitesMinimos = convitesMinimos;
    }

    public BigDecimal getDepositoMinimo() {
        return depositoMinimo;
    }

    public void setDepositoMinimo(BigDecimal depositoMinimo) {
        this.depositoMinimo = depositoMinimo;
    }
}
