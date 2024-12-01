package br.com.auto.bot.auth.repository;

import br.com.auto.bot.auth.generic.repository.GenericEntityDeletedRepository;
import br.com.auto.bot.auth.model.Contact;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactRepository extends GenericEntityDeletedRepository<Contact, Long> {
    Optional<Contact> findByIdAndUserId(Long id, Long idUser);
    @Query("SELECT c FROM Contact c WHERE c.userId = :userId AND c.isDeleted = false")
    List<Contact> findActiveContactsByUserId(@Param("userId") Long userId);
}
