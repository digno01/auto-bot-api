package br.gov.mme.auth.repository;

import br.gov.mme.auth.generic.repository.GenericEntityDeletedRepository;
import br.gov.mme.auth.model.Contact;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactRepository extends GenericEntityDeletedRepository<Contact, Long> {
    Optional<Contact> findByIdAndUserId(Long id, Long idUser);
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.isDeleted = false")
    List<Contact> findActiveContactsByUserId(@Param("userId") Long userId);
}
