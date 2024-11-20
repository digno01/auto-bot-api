package br.com.auto.bot.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_HISTORICO_TROCA_ROBO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoTrocaRobo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_HISTORICO_TROCA_ROBO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_ROBO_ORIGEM")
    private RoboInvestidor roboOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_ROBO_DESTINO")
    private RoboInvestidor roboDestino;

    @Column(name = "VL_SALDO_TRANSFERIDO")
    private BigDecimal saldoTransferido;

    @Column(name = "VL_RENDIMENTOS_INCORPORADOS")
    private BigDecimal rendimentosIncorporados;

    @Column(name = "DT_TROCA")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataTroca;

    @PrePersist
    protected void onCreate() {
        dataTroca = LocalDateTime.now();
    }
}
