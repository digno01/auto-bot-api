package br.com.auto.bot.auth.model;

import br.com.auto.bot.auth.enums.StatusInvestimento;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_INVESTIMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_INVESTIMENTO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @Column(name = "VL_INVESTIDO")
    private BigDecimal valorInvestido;

    @Column(name = "NU_PERCENTUAL_RENDIMENTO_DIARIO")
    private BigDecimal percentualRendimentoDiario;

    @Column(name = "DT_INICIO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataInicio;

    @Column(name = "DT_FIM")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataFim;

    @Column(name = "ST_INVESTIMENTO", length = 1)
    @Enumerated(EnumType.STRING)
    private StatusInvestimento status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_ROBO_INVESTIDOR")
    private RoboInvestidor roboInvestidor;

    @Column(name = "VL_ULTIMO_RENDIMENTO")
    private BigDecimal valorUltimoRendimento;

    @Column(name = "IS_ULTIMO_RENDIMENTO_LOSS")
    private Boolean isUltimoRendimentoLoss;

    @PrePersist
    protected void onCreate() {
        dataInicio = LocalDateTime.now();
    }
}