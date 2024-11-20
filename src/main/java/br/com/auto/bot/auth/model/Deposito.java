package br.com.auto.bot.auth.model;

import br.com.auto.bot.auth.enums.StatusDeposito;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "TB_DEPOSITO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deposito implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_DEPOSITO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @Column(name = "VL_DEPOSITO")
    private BigDecimal valorDeposito;

    @Column(name = "DT_DEPOSITO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataDeposito;

    @Column(name = "ST_DEPOSITO", length = 1)
    @Enumerated(EnumType.STRING)
    private StatusDeposito status;


    @Column(name = "DT_APROVACAO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataAprovacao;

    @PrePersist
    protected void onCreate() {
        dataDeposito = LocalDateTime.now();
        if (status == null) {
            status = StatusDeposito.P;
        }
    }
}
