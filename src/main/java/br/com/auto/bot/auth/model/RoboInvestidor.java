package br.com.auto.bot.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "TB_ROBO_INVESTIDOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoboInvestidor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_ROBO_INVESTIDOR")
    private Long id;

    @Column(name = "DS_NOME")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "apelido")
    private String apelido;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "NU_DIAS_PERIODO")
    private Integer diasPeriodo;

    @Column(name = "NU_NIVEL")
    private Integer nivel;

    @Column(name = "PC_RENDIMENTO_MIN")
    private BigDecimal percentualRendimentoMin;

    @Column(name = "PC_RENDIMENTO_MAX")
    private BigDecimal percentualRendimentoMax;

    @Column(name = "VL_INVESTIMENTO_MIN")
    private BigDecimal valorInvestimentoMin;

    @Column(name = "VL_INVESTIMENTO_MAX")
    private BigDecimal valorInvestimentoMax;

    @Column(name = "vl_ganho")
    private BigDecimal valorGanho;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;
}
