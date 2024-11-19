package br.gov.mme.auth.repository;

import br.gov.mme.auth.model.permissoes.PerfilAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilAcessoRepository extends JpaRepository<PerfilAcesso, Long> {


}
