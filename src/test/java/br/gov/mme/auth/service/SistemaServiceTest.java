package br.gov.mme.auth.service;

import br.gov.mme.auth.exceptions.RegistroNaoEncontradoException;
import br.gov.mme.auth.model.permissoes.Sistema;
import br.gov.mme.auth.repository.SistemaRepository;
import br.gov.mme.auth.service.SistemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class) // Usando a extensão Mockito com JUnit 5
public class SistemaServiceTest {

    @Mock
    private SistemaRepository repository;

    @InjectMocks
    private SistemaService service;

    private Sistema sistema;

    @BeforeEach
    void setUp() {
        // Inicializando um objeto Sistema de exemplo
        sistema = new Sistema();
        sistema.setId(1L);
        sistema.setSistema("Sistema1");
    }

    @Test
    public void testFindBySistema_Success() throws RegistroNaoEncontradoException {
        // Dado que o sistema existe no repositório
        Mockito.when(repository.findBySistema(anyString())).thenReturn(Optional.of(sistema));

        // Quando chamamos o método com um ID válido
        Sistema result = service.findBySistema("Sistema1");

        // Então o resultado não deve ser nulo e o nome do sistema deve ser "Sistema1"
        assertNotNull(result);
        assertEquals("Sistema1", result.getSistema());
        Mockito.verify(repository, Mockito.times(1)).findBySistema("Sistema1");
    }

    @Test
    public void testFindBySistema_ThrowsRegistroNaoEncontradoException() {
        // Dado que o sistema não é encontrado no repositório
        Mockito.when(repository.findBySistema(anyString())).thenReturn(Optional.empty());

        // Quando chamamos o método com um ID inválido, uma exceção deve ser lançada
        RegistroNaoEncontradoException exception = assertThrows(
                RegistroNaoEncontradoException.class,
                () -> service.findBySistema("SistemaInvalido")
        );

        // Verificando se a mensagem da exceção é a esperada
        assertEquals("Sistema informado não existe", exception.getMessage());
        Mockito.verify(repository, Mockito.times(1)).findBySistema("SistemaInvalido");
    }
}
