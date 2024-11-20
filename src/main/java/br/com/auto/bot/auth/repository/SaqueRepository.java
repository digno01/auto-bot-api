package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.Saque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SaqueRepository extends JpaRepository<Saque, Long> {

    @Query("SELECT COALESCE(SUM(s.valorSaque), 0) FROM Saque s " +
            "WHERE s.investimento.id = :investimentoId " +
            "AND s.status = 'APROVADO'")
    Optional<BigDecimal> findTotalSaquesByInvestimento(@Param("investimentoId") Long investimentoId);
}
