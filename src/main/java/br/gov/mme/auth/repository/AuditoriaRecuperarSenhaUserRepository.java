package br.gov.mme.auth.repository;

import br.gov.mme.auth.generic.repository.GenericEntityDeletedRepository;
import br.gov.mme.auth.model.AuditoriaRecuperarSenhaUser;
import br.gov.mme.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * @author fabricio.freitas
 * @since  2024
 */
@Repository
public interface AuditoriaRecuperarSenhaUserRepository extends JpaRepository<AuditoriaRecuperarSenhaUser, Long> {

}
