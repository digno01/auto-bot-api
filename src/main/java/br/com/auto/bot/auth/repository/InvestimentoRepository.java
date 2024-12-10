package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.projection.InvestimentoResumoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {


    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario.id = :userId " +
            "AND i.isLiberadoSaque = true " +
            "AND i.status IN ('A', 'PP') " +
            "AND i.saldoAtual > 0")
    List<Investimento> findWithdrawableInvestments(@Param("userId") Long userId);

    List<Investimento> findByUsuarioIdAndSaldoAtualGreaterThanAndDataLiberacaoLessThanEqualAndStatus(
            Long usuarioId,
            BigDecimal saldoAtual,
            LocalDateTime dataLiberacao,
            StatusInvestimento status);
    List<Investimento> findByUsuarioIdAndStatusAndDataLiberacaoLessThanEqualAndSaldoAtualGreaterThan(
            Long usuarioId,
            StatusInvestimento status,
            LocalDateTime dataLiberacao,
            BigDecimal saldoAtual);
    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario.id = :usuarioId " +
            "AND i.status IN ('A', 'PP') " +
            "AND i.dataInvestimento <= :dataAtual " +
            "AND i.dataLiberacao > :dataLimite " +
            "AND i.valorEfetuadoPIX >= i.roboInvestidor.valorInvestimentoMin")
    List<Investimento> findAllInvestimentosAtivosComSaldoByUsuarioId(
            @Param("usuarioId") Long usuarioId,
            @Param("dataLimite") LocalDateTime dataLimite,
            @Param("dataAtual") LocalDateTime dataAtual
    );


    @Query("SELECT i FROM Investimento i " +
            "WHERE i.id = :id " +
            "AND i.status = 'A' " +
            "AND i.usuario.id = :usuarioId ")
    Investimento findInvestimentoAtivoByUsuarioId(@Param("id") Long id, @Param("usuarioId") Long usuarioId);


    // No InvestimentoRepository:
    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario = :usuario " +
            "AND i.roboInvestidor = :robo " +
            "AND i.status IN :status " +
            "AND i.saldoAtual > 0")
    Optional<Investimento> findInvestimentoAtivoComSaldoByUsuarioAndRobo(
            @Param("usuario") User usuario,
            @Param("robo") RoboInvestidor robo,
            @Param("status") Collection<StatusInvestimento> status
    );


    // Busca investimento por usuário e status

    Optional<Investimento> findByUsuarioAndStatus(User usuario, StatusInvestimento status);

    @Query("SELECT i FROM Investimento i WHERE i.usuario = :usuario " +
            "AND i.roboInvestidor = :robo " +
            "AND i.status NOT IN ('F', 'C') " +
            "AND i.saldoAtual > 0")
    Optional<Investimento> findByUsuarioAndRoboAndStatusNotFinalizedOrCanceled(
            @Param("usuario") User usuario,
            @Param("robo") RoboInvestidor robo);

    List<Investimento> findByUsuarioIdAndStatusIn(Long usuarioId, List<StatusInvestimento> status);
    List<Investimento> findByUsuarioIdAndStatus(Long usuarioId, StatusInvestimento status);

    // Busca o investimento mais recente por usuário e status
    Optional<Investimento> findFirstByUsuarioAndStatusOrderByDataInvestimentoDesc(
            User usuario,
            StatusInvestimento status
    );

    // Busca investimentos por ID do usuário e status

    // Busca todos investimentos de um usuário por status ordenados por data
    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario = :usuario " +
            "AND i.status = :status " +
            "ORDER BY i.dataInvestimento DESC")
    List<Investimento> findAllByUsuarioAndStatusOrderByDataInvestimentoDesc(
            @Param("usuario") User usuario,
            @Param("status") String status
    );

    // Busca investimentos ativos em uma data específica para um usuário
    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario.id = :usuarioId " +
            "AND i.status = 'ATIVO' " +
            "AND i.dataInvestimento <= :dataReferencia " +
            "AND i.saldoAtual > 0 ")
    List<Investimento> findInvestimentosAtivosNaData(
            @Param("usuarioId") Long usuarioId,
            @Param("dataReferencia") LocalDateTime dataReferencia
    );

    // Verifica se existe investimento ativo para um usuário
    @Query("SELECT COUNT(i) > 0 FROM Investimento i " +
            "WHERE i.usuario = :usuario " +
            "AND i.status = 'ATIVO'")
    boolean existsInvestimentoAtivoByUsuario(@Param("usuario") User usuario);

    // Busca investimentos por usuário, robô e status
    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario = :usuario " +
            "AND i.roboInvestidor = :robo " +
            "AND i.status = :status")
    List<Investimento> findByUsuarioAndRoboAndStatus(
            @Param("usuario") User usuario,
            @Param("robo") RoboInvestidor robo,
            @Param("status") String status
    );

    // Gera resumo de investimentos por período

    @Query(value = "SELECT " +
            "r.ds_nome as nomeRobo, " +
            "COUNT(i.pk_investimento) as quantidadeInvestimentos, " +
            "COALESCE(SUM(i.VL_INICIAL), 0) as valorTotalInvestido " +
            "FROM TB_INVESTIMENTO i " +
            "INNER JOIN  TB_INVESTIMENTO r ON r.pk_robo_investidor = i.pk_robo_investidor " +
            "WHERE i.st_investimento = :status " +
            "AND i.DT_INVESTIMENTO BETWEEN :dataInicio AND :dataFim " +
            "GROUP BY r.ds_nome",
            nativeQuery = true)
    List<InvestimentoResumoProjection> gerarResumoInvestimentos(
            @Param("status") String status,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    // Busca investimentos com loss para compensação
    @Query("SELECT i FROM Investimento i " +
            "WHERE i.status = 'ATIVO' " +
            "AND i.dataInvestimento <= :dataLimite " +
            "AND i.isUltimoRendimentoLoss = true")
    List<Investimento> findInvestimentosComLossParaCompensacao(
            @Param("dataLimite") LocalDateTime dataLimite
    );

    // Atualiza status de investimentos em lote
    @Modifying
    @Query("UPDATE Investimento i SET i.status = :novoStatus " +
            "WHERE i.usuario = :usuario AND i.status = :statusAtual")
    void atualizarStatusInvestimentos(
            @Param("usuario") User usuario,
            @Param("statusAtual") String statusAtual,
            @Param("novoStatus") String novoStatus
    );

    boolean existsByIdTransacaoPagamentoGatewayAndUsuarioIdAndStatusIn(
            BigDecimal idTransacaoPagamentoGateway,
            Long usuarioId,
            Collection<StatusInvestimento> status
    );
    boolean existsByUsuarioIdAndRoboInvestidorIdAndStatus(Long usuarioId, Long roboId, StatusInvestimento status);

    Optional<Investimento> findFirstByIdTransacaoPagamentoGateway(BigDecimal idTransacao);
    @Query("SELECT i FROM Investimento i WHERE i.usuario = :usuario " +
            "AND i.roboInvestidor = :roboInvestidor " +
            "AND i.status IN ('P', 'R') " +
            "AND i.id != :investimentoId")
    List<Investimento> findPendingAndReinvestedByUsuarioAndRobo(
            @Param("usuario") User usuario,
            @Param("roboInvestidor") RoboInvestidor roboInvestidor,
            @Param("investimentoId") Long investimentoId
    );

    @Query("SELECT i FROM Investimento i " +
            "WHERE i.dataLiberacao <= :dataAtual " +
            "AND i.status IN ('A', 'PP') " +
            "AND (i.isLiberadoSaque = false OR i.isLiberadoSaque IS NULL)")
    List<Investimento> findByDataLiberacaoLessThanEqualAndIsLiberadoSaqueFalse(
            @Param("dataAtual") LocalDateTime dataAtual
    );


    @Query("SELECT i FROM Investimento i " +
            "WHERE i.usuario.id = :userId " +
            //"AND i.isLiberadoSaque = true " +
            "AND i.status <> 'C' " +
            "AND i.saldoAtual > 0 order by i.dataInvestimento desc ")
    List<Investimento> listarRendimentos(@Param("userId") Long userId);


    @Query("SELECT i FROM Investimento i WHERE i.usuario = :usuario")
    List<Investimento> findByUsuario(@Param("usuario") User usuario);

    Investimento findFirstByUsuarioOrderByDataInvestimentoAsc(User usuario);

    @Query("SELECT COUNT(i), SUM(i.valorInicial), SUM(i.saldoAtual) " +
            "FROM Investimento i WHERE i.usuario = :usuario")
    Object[] getInvestimentosSummary(@Param("usuario") User usuario);


    @Query("SELECT COUNT(i) FROM Investimento i WHERE i.valorEfetuadoPIX IS NOT NULL AND i.valorEfetuadoPIX > 0 AND i.usuario.id = :usuarioId")
    Integer countPaidInvestimentosWithPixValueByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT SUM(i.valorEfetuadoPIX) FROM Investimento i WHERE i.valorEfetuadoPIX IS NOT NULL AND i.valorEfetuadoPIX > 0  AND i.usuario.id = :usuarioId")
    BigDecimal sumValorPixPagosByUsuarioId(@Param("usuarioId") Long usuarioId);
}
