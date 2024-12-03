package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.enums.StatusSaque;
import br.com.auto.bot.auth.model.Saque;
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
public interface SaqueRepository extends JpaRepository<Saque, Long> {

    @Query("SELECT COALESCE(SUM(s.valorSaque), 0) FROM Saque s " +
            "WHERE s.investimento.id = :investimentoId " +
            "AND s.status = 'APROVADO'")
    Optional<BigDecimal> findTotalSaquesByInvestimento(@Param("investimentoId") Long investimentoId);

    List<Saque> findByUsuarioId(Long usuarioId);

    @Query("SELECT s FROM Saque s " +
            "JOIN FETCH s.investimento i " +
            "JOIN FETCH i.roboInvestidor r " +
            "WHERE s.status = :status")
    Page<Saque> findAllByStatus(@Param("status") StatusSaque status, Pageable pageable);
}
