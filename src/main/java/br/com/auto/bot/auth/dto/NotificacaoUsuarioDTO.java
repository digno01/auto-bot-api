package br.com.auto.bot.auth.dto;

import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.model.NotificacaoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoUsuarioDTO {
    private Long id;
    private String titulo;
    private String mensagem;
    private BigDecimal valorReferencia;
    private TipoNotificacao tipo;
    private Boolean lida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataLeitura;

    public static NotificacaoUsuarioDTO fromEntity(NotificacaoUsuario notificacao) {
        return new NotificacaoUsuarioDTO(
                notificacao.getId(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getValorReferencia(),
                notificacao.getTipo(),
                notificacao.getLida(),
                notificacao.getDataCriacao(),
                notificacao.getDataLeitura()
        );
    }
}