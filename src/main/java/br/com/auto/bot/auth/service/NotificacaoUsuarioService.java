package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.NotificacaoUsuarioDTO;
import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.NotificacaoUsuario;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.NotificacaoUsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class NotificacaoUsuarioService {

    @Autowired
    private NotificacaoUsuarioRepository repository;

    public void criarNotificacao(User usuario, String titulo, String mensagem,
                                 BigDecimal valor, TipoNotificacao tipo) {
        NotificacaoUsuario notificacao = NotificacaoUsuario.builder()
                .usuario(usuario)
                .titulo(titulo)
                .mensagem(mensagem)
                .valorReferencia(valor)
                .tipo(tipo)
                .build();

        repository.save(notificacao);
        log.info("Notificação criada para usuário {}: {}", usuario.getId(), titulo);
    }

    public Page<NotificacaoUsuarioDTO> listarNotificacoes(Long usuarioId, Boolean lida,
                                                          TipoNotificacao tipo, Pageable pageable) {
        Page<NotificacaoUsuario> notificacoes = repository.findByUsuarioIdAndFilters(
                usuarioId, lida, tipo, pageable);
        return notificacoes.map(NotificacaoUsuarioDTO::fromEntity);
    }

    public void marcarComoLida(Long notificacaoId) {
        NotificacaoUsuario notificacao = repository.findById(notificacaoId)
                .orElseThrow(() -> new BusinessException("Notificação não encontrada"));

        notificacao.setLida(true);
        notificacao.setDataLeitura(LocalDateTime.now());
        repository.save(notificacao);
    }

    public long contarNotificacoesNaoLidas(Long usuarioId) {
        return repository.countByUsuarioIdAndLida(usuarioId, false);
    }

    public Boolean contemNotificacaoInvestimento(Long id, TipoNotificacao investimentoPago) {
        return repository.contemNotificacaoInvestimento(id, investimentoPago);
    }
}
