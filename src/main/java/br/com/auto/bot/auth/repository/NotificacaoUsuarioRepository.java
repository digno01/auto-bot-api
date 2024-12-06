package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.model.NotificacaoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoUsuarioRepository extends JpaRepository<NotificacaoUsuario, Long> {

    @Query("SELECT n FROM NotificacaoUsuario n " +
            "WHERE n.usuario.id = :usuarioId " +
            "AND (:lida IS NULL OR n.lida = :lida) " +
            "AND (:tipo IS NULL OR n.tipo = :tipo)")
    Page<NotificacaoUsuario> findByUsuarioIdAndFilters(
            @Param("usuarioId") Long usuarioId,
            @Param("lida") Boolean lida,
            @Param("tipo") TipoNotificacao tipo,
            Pageable pageable
    );

    long countByUsuarioIdAndLida(Long usuarioId, Boolean lida);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NotificacaoUsuario n WHERE n.usuario.id = :idUsuario AND n.tipo = :tipoNotificacao")
    Boolean contemNotificacaoInvestimento(@Param("idUsuario") Long idUsuario, @Param("tipoNotificacao") TipoNotificacao tipoNotificacao);
}