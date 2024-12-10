package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.Indicacao;
import br.com.auto.bot.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndicacaoRepository extends JpaRepository<Indicacao, Long> {
    Optional<Indicacao> findByUsuarioAndIsActiveTrue(User usuario);
    List<Indicacao> findByUsuarioIndicadorAndIsActiveTrue(User usuarioIndicador);

    @Query("SELECT i FROM Indicacao i WHERE i.nivel = :nivel AND i.usuario = :usuario AND i.isActive = true ORDER BY i.createdAt DESC")
    Optional<Indicacao> findFirstIndicadorByNivelAndUsuario(
            @Param("nivel") Integer nivel,
            @Param("usuario") User usuario
    );

    List<Indicacao> findByUsuarioIndicador(User usuarioIndicador);
}
