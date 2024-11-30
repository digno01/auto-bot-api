package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.repository.RoboInvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoboInvestidorService {

    @Autowired
    private RoboInvestidorRepository repository;

    public Page<RoboInvestidor> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public RoboInvestidor findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RoboInvestidor não encontrado com ID: " + id));
    }

    public RoboInvestidor save(RoboInvestidor roboInvestidor) {
        return repository.save(roboInvestidor);
    }

    public RoboInvestidor update(Long id, RoboInvestidor roboInvestidor) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("RoboInvestidor não encontrado com ID: " + id);
        }
        roboInvestidor.setId(id);
        return repository.save(roboInvestidor);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("RoboInvestidor não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}