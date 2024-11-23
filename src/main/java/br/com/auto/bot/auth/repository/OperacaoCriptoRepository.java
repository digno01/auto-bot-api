package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.OperacaoCripto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperacaoCriptoRepository extends JpaRepository<OperacaoCripto, Long> {

    // Buscar operações por rendimento
    List<OperacaoCripto> findByRendimentoId(Long rendimentoId);

    // Buscar operações por usuário e período
    @Query("SELECT o FROM OperacaoCripto o " +
            "WHERE o.rendimento.usuario.id = :usuarioId " +
            "AND o.dataCompra BETWEEN :inicio AND :fim")
    List<OperacaoCripto> findByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    // Buscar operações por moeda
    List<OperacaoCripto> findByMoedaOrderByDataCompraDesc(String moeda);

    // Buscar operações lucrativas
    @Query("SELECT o FROM OperacaoCripto o WHERE o.valorLucro > 0")
    List<OperacaoCripto> findOperacoesLucrativas();

    // Calcular lucro total por moeda
    @Query("SELECT o.moeda, SUM(o.valorLucro) as lucroTotal " +
            "FROM OperacaoCripto o " +
            "GROUP BY o.moeda")
    List<Object[]> calcularLucroPorMoeda();

    // Buscar operações com maior lucro
    @Query("SELECT o FROM OperacaoCripto o " +
            "ORDER BY o.valorLucro DESC")
    List<OperacaoCripto> findOperacoesMaiorLucro(org.springframework.data.domain.Pageable pageable);


    // Buscar operações por faixa de lucro
    @Query("SELECT o FROM OperacaoCripto o " +
            "WHERE o.valorLucro BETWEEN :lucroMin AND :lucroMax")
    List<OperacaoCripto> findByFaixaLucro(
            @Param("lucroMin") BigDecimal lucroMin,
            @Param("lucroMax") BigDecimal lucroMax
    );

    // Contar operações por moeda
    @Query("SELECT o.moeda, COUNT(o) as quantidade " +
            "FROM OperacaoCripto o " +
            "GROUP BY o.moeda")
    List<Object[]> contarOperacoesPorMoeda();

    // Buscar últimas operações por usuário
    @Query("SELECT o FROM OperacaoCripto o " +
            "WHERE o.rendimento.usuario.id = :usuarioId " +
            "ORDER BY o.dataCompra DESC")
    List<OperacaoCripto> findUltimasOperacoesUsuario(
            @Param("usuarioId") Long usuarioId,
            org.springframework.data.domain.Pageable pageable
    );
}
