package br.gov.mme.auth.repository;

import br.gov.mme.auth.generic.repository.GenericEntityDeletedRepository;
import br.gov.mme.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository  extends GenericEntityDeletedRepository<User, Long> {
    User findByCpf(String cpf);

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByToken(String token);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND SIZE(u.perfilAcesso) > 0")
    Page<User> findAllByIsDeletedFalseAndHasPerfilAcesso(Pageable pageable);
}
