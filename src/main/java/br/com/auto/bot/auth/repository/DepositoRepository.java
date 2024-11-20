package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.dto.DepositoEstatisticaDTO;
import br.com.auto.bot.auth.model.Deposito;
import br.com.auto.bot.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Long> {

    // Buscar depósitos por usuário e status
    List<Deposito> findByUsuarioAndStatus(User usuario, String status);

    // Buscar depósito específico por ID e usuário
    Optional<Deposito> findByIdAndUsuario(Long id, User usuario);

    // Buscar depósitos pendentes
    List<Deposito> findByStatus(String status);

    // Buscar depósitos por período
    List<Deposito> findByDataDepositoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar depósitos por usuário e período
    List<Deposito> findByUsuarioAndDataDepositoBetween(
            User usuario,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    // Soma total de depósitos aprovados por usuário
    @Query("SELECT COALESCE(SUM(d.valorDeposito), 0) FROM Deposito d " +
            "WHERE d.usuario = :usuario AND d.status = 'APROVADO'")
    BigDecimal somaTotalDepositosAprovados(@Param("usuario") User usuario);

    // Buscar últimos depósitos do usuário
    @Query("SELECT d FROM Deposito d " +
            "WHERE d.usuario = :usuario " +
            "ORDER BY d.dataDeposito DESC")
    List<Deposito> findLastDepositos(
            @Param("usuario") User usuario,
            org.springframework.data.domain.Pageable pageable
    );

    // Contar depósitos pendentes por usuário
    @Query("SELECT COUNT(d) FROM Deposito d " +
            "WHERE d.usuario = :usuario AND d.status = 'PENDENTE'")
    Long countDepositosPendentes(@Param("usuario") User usuario);

    // Buscar depósitos aguardando aprovação há mais de X horas
    @Query("SELECT d FROM Deposito d " +
            "WHERE d.status = 'PENDENTE' " +
            "AND d.dataDeposito < :dataLimite")
    List<Deposito> findDepositosAguardandoAprovacao(
            @Param("dataLimite") LocalDateTime dataLimite
    );


    // Buscar depósitos que precisam de atenção (pendentes há muito tempo)
    @Query("SELECT d FROM Deposito d " +
            "WHERE d.status = 'PENDENTE' " +
            "AND d.dataDeposito < :dataLimite " +
            "ORDER BY d.dataDeposito ASC")
    List<Deposito> findDepositosAtencao(@Param("dataLimite") LocalDateTime dataLimite);

    // Verificar se usuário tem depósito pendente duplicado (mesmo valor em curto período)
    @Query("SELECT COUNT(d) > 0 FROM Deposito d " +
            "WHERE d.usuario = :usuario " +
            "AND d.valorDeposito = :valor " +
            "AND d.status = 'PENDENTE' " +
            "AND d.dataDeposito > :dataLimite")
    boolean existeDepositoPendenteDuplicado(
            @Param("usuario") User usuario,
            @Param("valor") BigDecimal valor,
            @Param("dataLimite") LocalDateTime dataLimite
    );
}

// DTO para estatísticas

