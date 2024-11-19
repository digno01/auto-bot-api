package br.gov.mme.auth.generic;

import br.gov.mme.auth.exceptions.RegistroDuplicadoException;
import br.gov.mme.auth.exceptions.RegistroNaoEncontradoException;
import br.gov.mme.auth.generic.interfaces.IActiveTable;
import br.gov.mme.auth.generic.interfaces.IDeletedTable;
import br.gov.mme.auth.generic.service.GenericService;
import br.gov.mme.auth.util.CustomPageable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


public abstract class GenericController<T, TO, ID extends Long> {
    protected final GenericService<T, ID> service;
    @Autowired
    protected ModelMapper modelMapper;

    public GenericController(GenericService<T, ID> service) {
        this.service = service;
    }

    protected abstract T toEntity(TO var1);

    protected abstract TO toTO(T var1);

    public ResponseEntity<Page<TO>> getAll(CustomPageable pageable, HttpServletRequest httpServletRequest) {
        Page<T> entities = this.service.findAll(pageable.toPageable());
        Page<TO> tos = entities.map(this::toTO);
        return ResponseEntity.ok(tos);
    }


    @GetMapping
    public ResponseEntity<Page<TO>> getAll(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDirection, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        Sort sort = Sort.by(Direction.fromString(sortDirection), new String[]{sortBy});
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<T> entities = this.service.findAll(pageable);
        Page<TO> tos = entities.map(this::toTO);
        return ResponseEntity.ok(tos);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<TO> getById(@PathVariable ID id, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        return (ResponseEntity)this.service.findById(id).map((entity) -> {
            return ResponseEntity.ok(this.toTO(entity));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TO> create(@RequestBody TO to) throws Exception, RegistroDuplicadoException {
        T entity = this.toEntity(to);
        T savedEntity = this.service.save(entity);
        TO savedTO = this.toTO(savedEntity);
        return ResponseEntity.ok(savedTO);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<TO> update(@PathVariable ID id, @RequestBody TO to, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        T entity = this.toEntity(to);
        T updatedEntity = this.service.update(id, entity);
        TO updatedTO = this.toTO(updatedEntity);
        return ResponseEntity.ok(updatedTO);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<TO> delete(@PathVariable ID id) {
        Optional<T> entityOptional = this.service.findById(id);
        if (entityOptional.isPresent()) {
            T entity = entityOptional.get();

            try {
                T deletedEntity = this.service.deleteById(id);
                if (deletedEntity instanceof IDeletedTable && !((IDeletedTable)deletedEntity).getIsDeleted()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                } else {
                    TO deletedTO = this.toTO(deletedEntity);
                    return ResponseEntity.ok(deletedTO);
                }
            } catch (EntityNotFoundException var6) {
                return ResponseEntity.ok().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping({"/{id}/active-deactive/{flag}"})
    public ResponseEntity<TO> toggleActiveDeactive(@PathVariable ID id, @PathVariable Boolean flag) {
        Optional<T> entityOptional = this.service.findById(id);
        if (entityOptional.isPresent()) {
            T entity = entityOptional.get();
            if (entity instanceof IActiveTable) {
                this.service.activeDeactiveEntity(entityOptional.get(), flag);
                TO updatedTO = this.toTO(entity);
                return ResponseEntity.ok(updatedTO);
            } else {
                return (ResponseEntity<TO>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
