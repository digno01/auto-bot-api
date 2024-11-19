package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.Indicacao;
import br.com.auto.bot.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndicacaoRepository extends JpaRepository<Indicacao, Long> {
    Optional<Indicacao> findByUsuarioAndIsActiveTrue(User usuario);
    List<Indicacao> findByUsuarioIndicadorAndIsActiveTrue(User usuarioIndicador);
}
