package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.HistoricoTrocaRobo;
import br.com.auto.bot.auth.model.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricoTrocaRoboRepository extends JpaRepository<HistoricoTrocaRobo, Long> {

    List<HistoricoTrocaRobo> findByUsuarioOrderByDataTrocaDesc(User usuario);

    @Query("SELECT h FROM HistoricoTrocaRobo h " +
            "WHERE h.usuario.id = :usuarioId " +
            "AND h.dataTroca BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY h.dataTroca DESC")
    List<HistoricoTrocaRobo> findByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("SELECT COUNT(h) FROM HistoricoTrocaRobo h " +
            "WHERE h.usuario.id = :usuarioId " +
            "AND h.dataTroca > :dataLimite")
    Long countTrocasNoPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataLimite") LocalDateTime dataLimite
    );

    @Query("SELECT h FROM HistoricoTrocaRobo h " +
            "WHERE h.usuario.id = :usuarioId " +
            "AND h.roboDestino.id = :roboId " +
            "ORDER BY h.dataTroca DESC")
    List<HistoricoTrocaRobo> findTrocasParaRobo(
            @Param("usuarioId") Long usuarioId,
            @Param("roboId") Long roboId
    );

}
