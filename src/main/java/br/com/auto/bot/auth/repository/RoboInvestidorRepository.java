package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.model.RoboInvestidor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoboInvestidorRepository extends JpaRepository<RoboInvestidor, Long> {

    List<RoboInvestidor> findByIsActiveTrue();

    @Query("SELECT r FROM RoboInvestidor r " +
            "WHERE r.isActive = true " +
            "AND r.valorInvestimentoMin <= :valorInvestimento " +
            "AND r.valorInvestimentoMax >= :valorInvestimento")
    List<RoboInvestidor> findRobosDisponiveisParaValor(
            @Param("valorInvestimento") BigDecimal valorInvestimento
    );

    @Query("SELECT r FROM RoboInvestidor r " +
            "WHERE r.isActive = true " +
            "AND r.valorInvestimentoMin <= :valorTotal " +
            "AND r.valorInvestimentoMax >= :valorTotal " +
            "AND r.diasPeriodo >= :periodoMinimo")
    List<RoboInvestidor> findRobosDisponiveisParaTroca(
            @Param("valorTotal") BigDecimal valorTotal,
            @Param("periodoMinimo") Integer periodoMinimo
    );

    Optional<RoboInvestidor> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT DISTINCT r FROM RoboInvestidor r " +
            "JOIN Investimento i ON i.roboInvestidor = r " +
            "WHERE i.usuario.id = :usuarioId " +
            "AND i.status = 'ATIVO'")
    Optional<RoboInvestidor> findRoboAtivoByUsuario(@Param("usuarioId") Long usuarioId);



    @Query("SELECT r FROM RoboInvestidor r WHERE (NOT EXISTS (SELECT 1 FROM NotificacaoUsuario n WHERE n.usuario.id = :idUsuario AND n.tipo = :tipoNotificacao)) OR (r.id <> 1 AND EXISTS (SELECT 1 FROM NotificacaoUsuario n WHERE n.usuario = :usuario AND n.tipo = :tipoNotificacao))")
    List<RoboInvestidor> findRobosBasedOnNotification(@Param("idUsuario") Long idUsuario, @Param("tipoNotificacao") TipoNotificacao tipoNotificacao);

    Page<RoboInvestidor> findByNivelAndIsActiveTrue(Integer nivel, Pageable pageable);
}


