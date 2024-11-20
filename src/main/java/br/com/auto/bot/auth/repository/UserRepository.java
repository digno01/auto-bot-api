package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.generic.repository.GenericEntityDeletedRepository;
import br.com.auto.bot.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository  extends GenericEntityDeletedRepository<User, Long> {
    User findByCpf(String cpf);

    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByToken(String token);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND SIZE(u.perfilAcesso) > 0")
    Page<User> findAllByIsDeletedFalseAndHasPerfilAcesso(Pageable pageable);

    List<User>  findByIsActiveTrueAndSaldoInvestidoGreaterThan(BigDecimal saldo);

    Optional<User> findByCodigoIndicacao(String codigoIndicacao);
}
