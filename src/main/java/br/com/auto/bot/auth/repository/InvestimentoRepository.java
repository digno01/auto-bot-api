package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {
    Optional<Investimento> findFirstByUsuarioAndStatusOrderByDataInicioDesc(User usuario, String status);
}
