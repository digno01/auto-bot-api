package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.model.permissoes.PerfilAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilAcessoRepository extends JpaRepository<PerfilAcesso, Long> {


}
