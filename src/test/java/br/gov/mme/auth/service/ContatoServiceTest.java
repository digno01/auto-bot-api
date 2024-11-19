package br.gov.mme.auth.service;

import br.gov.mme.auth.mock.ContatoMock;
import br.gov.mme.auth.mock.UserMock;
import br.gov.mme.auth.model.Contact;
import br.gov.mme.auth.repository.ContactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
public class ContatoServiceTest {
    @InjectMocks
    private ContactService service;
    @Mock
    private ContactRepository repository;

    @Mock
    private JpaRepository<Contact, Long> repositorybean;
    @BeforeEach
    void init() {
        Optional<Contact> contatoOptional = Optional.of(ContatoMock.getContato(UserMock.getUser()));
        when(repository.findByIdAndUserId(Long.valueOf(1),Long.valueOf(1))).thenReturn(contatoOptional);
        when(repository.findById(Long.valueOf(1))).thenReturn(contatoOptional);
    }
    @Test
    void createTestSucesso() {
        var contato = ContatoMock.getContatoDTO();
        contato.setId(1);
        Assertions.assertDoesNotThrow(() ->
                service.saveOrUpdate(List.of(contato), UserMock.getUser())
        );
    }

    @Test
    void createTestNotPresent() {
        when(repository.findByIdAndUserId(Long.valueOf(1),Long.valueOf(1))).thenReturn(Optional.empty());
        var contato = ContatoMock.getContatoDTO();
        contato.setId(1);
        Assertions.assertDoesNotThrow(() ->
                service.saveOrUpdate(List.of(contato), UserMock.getUser())
        );
    }

    @Test
    void createTestDelete() {
        var contato = ContatoMock.getContatoDTO();
        contato.setRemove(true);
        contato.setId(1);

        Assertions.assertDoesNotThrow(() ->
                service.saveOrUpdate(List.of(contato), UserMock.getUser())
        );
    }


}

