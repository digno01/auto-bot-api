package br.com.auto.bot.auth.generic.service;


import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.generic.repository.GenericEntityDeletedRepository;
import br.com.auto.bot.auth.generic.interfaces.IDeletedTable;
import br.com.auto.bot.auth.generic.interfaces.IActiveTable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Service
public abstract class GenericService<T, ID> {
    @Autowired
    protected JpaRepository<T, ID> repository;

    public GenericService() {
    }

    @Transactional(
            readOnly = true,
            propagation = Propagation.NOT_SUPPORTED
    )
    public List<T> findAll() {
        return this.repository instanceof GenericEntityDeletedRepository<T,ID> ? ((GenericEntityDeletedRepository)this.repository).findAllByIsDeletedFalse() : this.repository.findAll();
    }

    @Transactional(
            readOnly = true,
            propagation = Propagation.NOT_SUPPORTED
    )
    public Page<T> findAll(Pageable pageable) {
        return this.repository instanceof GenericEntityDeletedRepository ? ((GenericEntityDeletedRepository)this.repository).findAllByIsDeletedFalse(pageable) : this.repository.findAll(pageable);
    }

    @Transactional(
            readOnly = true,
            propagation = Propagation.NOT_SUPPORTED
    )
    public Optional<T> findById(ID id) {
        return this.repository.findById(id);
    }

    @Transactional
    public T save(T entity) {
        return this.repository.save(entity);
    }

    @Transactional
    public T update(ID id, T updatedEntity) {
        Optional<T> optionalEntity = this.repository.findById(id);
        if (!optionalEntity.isPresent()) {
            throw new BusinessException("Entity with id " + id + " not found");
        } else {
            T existingEntity = optionalEntity.get();
            Field[] fields = updatedEntity.getClass().getDeclaredFields();
            Field[] var6 = fields;
            int var7 = fields.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Field field = var6[var8];
                if (!field.getName().equals("id")) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(updatedEntity);
                        if (value != null) {
                            field.set(existingEntity, value);
                        }
                    } catch (IllegalAccessException var11) {
                        var11.printStackTrace();
                    }
                }
            }

            return this.repository.save(existingEntity);
        }
    }

    @Transactional
    public T deleteById(ID id) {
        Optional<T> entityOptional = this.repository.findById(id);
        if (entityOptional.isPresent()) {
            T entity = entityOptional.get();
            if (entity instanceof IDeletedTable) {
                ((IDeletedTable)entity).setIsDeleted(true);
                this.repository.save(entity);
            } else {
                this.repository.deleteById(id);
            }

            return entity;
        } else {
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
    }

    @Transactional
    public T activeDeactiveEntity(T entity, Boolean flag) {
        if (entity instanceof IActiveTable) {
            ((IActiveTable)entity).setIsActive(flag);
            this.repository.save(entity);
        }

        return entity;
    }

}
