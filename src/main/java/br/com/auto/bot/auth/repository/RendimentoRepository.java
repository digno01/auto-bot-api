package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.enums.TipoResultado;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.Rendimento;
import br.com.auto.bot.auth.model.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendimentoRepository extends JpaRepository<Rendimento, Long> {
    List<Rendimento> findByUsuarioAndDataRendimentoBetween(User usuario, LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT r FROM Rendimento r WHERE r.usuario.id = :usuarioId AND r.dataRendimento BETWEEN :inicio AND :fim order by r.dataRendimento desc")
    List<Rendimento> findRendimentosPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT r FROM Rendimento r " +
            "WHERE r.usuario.id = :usuarioId " +
            "AND r.tipoResultado = :tipoResultado " +
            "AND r.dataRendimento BETWEEN :inicio AND :fim")
    List<Rendimento> findByUsuarioAndTipoResultado(
            @Param("usuarioId") Long usuarioId,
            @Param("tipoResultado") TipoResultado tipoResultado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    List<Rendimento> findByInvestimentoAndDataRendimentoBetweenOrderByDataRendimentoDesc(
            Investimento investimento,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal
    );
}

