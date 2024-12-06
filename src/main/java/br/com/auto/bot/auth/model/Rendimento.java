package br.com.auto.bot.auth.model;

import br.com.auto.bot.auth.converter.TipoRendimentoConverter;
import br.com.auto.bot.auth.converter.TipoResultadoConverter;
import br.com.auto.bot.auth.enums.TipoRendimento;
import br.com.auto.bot.auth.enums.TipoResultado;
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
    @JoinColumn(name = "PK_INVESTIMENTO")
    private Investimento investimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @Column(name = "VL_RENDIMENTO")
    private BigDecimal valorRendimento;

    @Column(name = "VL_ACUMULADO")
    private BigDecimal valorAcumulado;

    @Column(name = "DT_RENDIMENTO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataRendimento;

    @Column(name = "TP_RENDIMENTO", length = 1)
    @Convert(converter = TipoRendimentoConverter.class)
    private TipoRendimento tipoRendimento;

    @Column(name = "PC_RENDIMENTO")
    private BigDecimal percentualRendimento;

    @Column(name = "TP_RESULTADO", length = 1)
    @Convert(converter = TipoResultadoConverter.class)
    private TipoResultado tipoResultado;

    @PrePersist
    protected void onCreate() {
        dataRendimento = LocalDateTime.now();
        tipoResultado = valorRendimento.compareTo(BigDecimal.ZERO) >= 0 ?
                TipoResultado.LUCRO : TipoResultado.PERDA;
    }
}


