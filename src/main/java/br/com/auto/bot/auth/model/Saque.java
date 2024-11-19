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
@Table(name = "TB_SAQUE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Saque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_SAQUE")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @Column(name = "VL_SAQUE")
    private BigDecimal valorSaque;

    @Column(name = "DT_SOLICITACAO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataSolicitacao;

    @Column(name = "DT_PROCESSAMENTO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataProcessamento;

    @Column(name = "ST_SAQUE")
    private String status;

    @Column(name = "DS_DADOS_BANCARIOS")
    private String dadosBancarios;

    @PrePersist
    protected void onCreate() {
        dataSolicitacao = LocalDateTime.now();
    }
}
