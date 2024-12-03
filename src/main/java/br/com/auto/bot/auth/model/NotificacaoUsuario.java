package br.com.auto.bot.auth.model;

import br.com.auto.bot.auth.enums.TipoNotificacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NOTIFICACAO_USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoUsuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_NOTIFICACAO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_USUARIO")
    private User usuario;

    @Column(name = "DS_TITULO", length = 100)
    private String titulo;

    @Column(name = "DS_MENSAGEM", length = 500)
    private String mensagem;

    @Column(name = "VL_REFERENCIA")
    private BigDecimal valorReferencia;

    @Column(name = "TP_NOTIFICACAO", length = 30)
    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    @Column(name = "ST_LIDA")
    private Boolean lida;

    @Column(name = "DT_CRIACAO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataCriacao;

    @Column(name = "DT_LEITURA")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dataLeitura;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (lida == null) {
            lida = false;
        }
    }
}
