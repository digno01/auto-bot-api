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
@Table(name = "TB_RENDIMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rendimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_RENDIMENTO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_INVESTIMENTO", nullable = true) // Permite null
    private Investimento investimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @Column(name = "VL_RENDIMENTO")
    private BigDecimal valorRendimento;

    @Column(name = "DT_RENDIMENTO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataRendimento;

    @Column(name = "TP_RENDIMENTO")
    private String tipoRendimento;

    @Column(name = "PC_RENDIMENTO")
    private BigDecimal percentualRendimento;  // Nova coluna

    @PrePersist
    protected void onCreate() {
        dataRendimento = LocalDateTime.now();
    }
}
