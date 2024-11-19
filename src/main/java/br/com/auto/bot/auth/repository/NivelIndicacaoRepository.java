package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.NivelIndicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NivelIndicacaoRepository extends JpaRepository<NivelIndicacao, Long> {
    Optional<NivelIndicacao> findByNivelAndIsActiveTrue(Integer nivel);
}
