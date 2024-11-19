package br.gov.mme.auth.repository;

import br.gov.mme.auth.model.UsuarioPerfil;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioPerfilRepository extends CrudRepository<UsuarioPerfil, Long> {

    @Modifying
    @Query(value = "delete tup from TB_USUARIO_PERFIL tup " +
            "inner join TB_PERFIL_ACESSO tpa ON tup.PK_PERFIL_ACESSO = tpa.PK_PERFIL_ACESSO " +
            "where tup.PK_USUARIO = :idUsuario and tpa.PK_SISTEMA = :idSistema", nativeQuery = true)
    void deletarTodosAcessoPerfilPorSistema(Long idUsuario, Long idSistema);

    @Modifying
    @Query(value = "INSERT INTO TB_USUARIO_PERFIL (PK_USUARIO, PK_PERFIL_ACESSO) VALUES(:idUsuario, :idPerfil);", nativeQuery = true)
    void inserirPerfilDeAcordoComSistemaNoUsuario(Long idUsuario, Long idPerfil);
}
