package br.gov.mme.auth.service;

import br.gov.mme.auth.repository.UsuarioPerfilRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class) // Usando a extensão do Mockito com JUnit 5
public class UsuarioPerfilServiceTest {

    @Mock
    private UsuarioPerfilRepository usuarioPerfilRepository;

    @InjectMocks
    private UsuarioPerfilService usuarioPerfilService;

    @Test
    public void testDeletarTodosAcessoPerfilPorSistema() {
        // Quando o método deletarTodosAcessoPerfilPorSistema é chamado
        usuarioPerfilService.deletarTodosAcessoPerfilPorSistema(1L, 2L);

        // Verifica se o repositório foi chamado com os parâmetros corretos
        Mockito.verify(usuarioPerfilRepository, times(1))
                .deletarTodosAcessoPerfilPorSistema(1L, 2L);
    }

    @Test
    public void testInserirPerfilDeAcordoComSistemaNoUsuario() {
        // Quando o método inserirPerfilDeAcordoComSistemaNoUsuario é chamado
        usuarioPerfilService.inserirPerfilDeAcordoComSistemaNoUsuario(1L, 3L);

        // Verifica se o repositório foi chamado com os parâmetros corretos
        Mockito.verify(usuarioPerfilRepository, times(1))
                .inserirPerfilDeAcordoComSistemaNoUsuario(1L, 3L);
    }

}

