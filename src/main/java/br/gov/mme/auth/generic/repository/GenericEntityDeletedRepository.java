package br.gov.mme.auth.generic.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface GenericEntityDeletedRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    List<T> findAllByIsDeletedFalse();

    Page<T> findAllByIsDeletedFalse(Pageable var1);

    Optional<T> findByIdAndIsDeletedFalse(ID var1);
}
