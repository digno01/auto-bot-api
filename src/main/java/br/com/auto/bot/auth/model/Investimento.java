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

    @Column(name = "ST_INVESTIMENTO")
    private String status;

    @PrePersist
    protected void onCreate() {
        dataInicio = LocalDateTime.now();
    }
}