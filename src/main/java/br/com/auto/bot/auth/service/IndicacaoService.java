package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.model.Indicacao;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.IndicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IndicacaoService {

    @Autowired
    private IndicacaoRepository repository;

    public Indicacao save(Indicacao indicacao) {
        indicacao.setCreatedAt(LocalDateTime.now());
        indicacao.setIsActive(true);
        return repository.save(indicacao);
    }

    public Optional<Indicacao> findByUsuario(User usuario) {
        return repository.findByUsuarioAndIsActiveTrue(usuario);
    }

    public Optional<Indicacao> findFirstIndicadorByNivelAndUsuario(int i, User user) {
        return repository.findFirstIndicadorByNivelAndUsuario(i, user);
    }
}
