package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.NotificacaoUsuarioRepository;
import br.com.auto.bot.auth.repository.RoboInvestidorRepository;
import br.com.auto.bot.auth.repository.UserRepository;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class RoboInvestidorService {

    @Autowired
    private RoboInvestidorRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificacaoUsuarioRepository notificacaoUsuarioRepository;

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

    public List<RoboInvestidor> getRobosInvestidorForUser() {
        User usuario = userRepository.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        List<RoboInvestidor>  robos =  repository.findRobosByNivelMenorOuIgualAoNivelUsuario(usuario.getNivelConta());
        Boolean contemInvestimentoAprovado = notificacaoUsuarioRepository.contemNotificacaoInvestimento(ObterDadosUsuarioLogado.getUsuarioLogadoId(), TipoNotificacao.INVESTIMENTO_PAGO);
        if (contemInvestimentoAprovado) {
            robos.removeIf(robo -> robo.getId().equals(1L)); // Remove o robô com ID 1
        }
        return robos;
    }

    public Page<RoboInvestidor> findByNivel(Integer nivel, Pageable pageable) {
        return repository.findByNivelAndIsActiveTrue(nivel, pageable);
    }
}