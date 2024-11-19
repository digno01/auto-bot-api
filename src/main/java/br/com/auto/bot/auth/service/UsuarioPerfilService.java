package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.repository.UsuarioPerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioPerfilService {

    @Autowired
    private UsuarioPerfilRepository usuarioPerfilRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletarTodosAcessoPerfilPorSistema(Long idUsuario, Long idSistema) {
        usuarioPerfilRepository.deletarTodosAcessoPerfilPorSistema(idUsuario, idSistema);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inserirPerfilDeAcordoComSistemaNoUsuario(Long idUsuario, Long idPerfil) {
        usuarioPerfilRepository.inserirPerfilDeAcordoComSistemaNoUsuario(idUsuario, idPerfil);
    }
}
