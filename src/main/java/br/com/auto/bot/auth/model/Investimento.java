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

    @Column(name = "ID_TRANSACTION_GATEWAY")
    private BigDecimal idTransacaoPagamentoGateway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_ROBO_INVESTIDOR")
    private RoboInvestidor roboInvestidor;

    @Column(name = "VL_INICIAL")
    private BigDecimal valorInicial;

    @Column(name = "SALDO_ATUAL")
    private BigDecimal saldoAtual;

    @Column(name = "DT_INVESTIMENTO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataInvestimento;

    @Column(name = "DT_LIBERACAO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataLiberacao;

    @Column(name = "ST_INVESTIMENTO", length = 1)
    @Enumerated(EnumType.STRING)
    private StatusInvestimento status;

    @Column(name = "IS_LIBERADO_SAQUE")
    private Boolean isLiberadoSaque;

    @Column(name = "IS_ULTIMO_RENDIMENTO_LOSS")
    private Boolean isUltimoRendimentoLoss;

    @Column(name = "DT_CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataCadastro;

    @Column(name = "DT_UPDATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataAlteracao;

    @Column(name = "url_qr_code")
    private String urlQrcode;

    @PrePersist
    protected void onCreate() {
        dataInvestimento = LocalDateTime.now();
        dataCadastro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAlteracao = LocalDateTime.now();
    }
}
