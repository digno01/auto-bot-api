package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.EmailUtil;
import br.com.auto.bot.auth.dto.UserDTO;
import br.com.auto.bot.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private EmailService emailService;

    private User user;

    @BeforeEach
    public void setUp() {
        // Configuração inicial do usuário de teste
        user = new User();
        user.setEmail("test@example.com");
        user.setNome("Test User");
        user.setToken("sampleToken");
    }

    @Test
    public void testSendRecoveryEmail() {
        // Configurar comportamento mockado
        when(messageSource.getMessage(eq("MI001"), any(), any())).thenReturn("Subject for recovery email");
        when(templateEngine.process(eq("mailPasswordRecovery"), any(Context.class))).thenReturn("Email body");

        // Executar o método
        emailService.sendRecoveryEmail(user);
    }

    @Test
    public void testEnviaEmailSenhaALteradaSucesso() {
        // Configurar comportamento mockado
        when(messageSource.getMessage(eq("MI002"), any(), any())).thenReturn("Subject for password changed email");
        when(templateEngine.process(eq("senhaAlteradaSucesso"), any(Context.class))).thenReturn("Success email body");

        // Executar o método
        emailService.enviaEmailSenhaALteradaSucesso(user);

    }

    @Test
    public void testEnvioEmailAtivacaoConta() {
        // Configurar comportamento mockado
        when(messageSource.getMessage(eq("MI003"), any(), any())).thenReturn("Subject for account activation email");
        when(templateEngine.process(eq("ativacaoContaEmail"), any(Context.class))).thenReturn("Activation email body");

        // Executar o método
        emailService.envioEmailAtivacaoConta(user);

    }

    @Test
    public void testEnviarEmailComSenha() {
        // Configurar comportamento mockado
        when(messageSource.getMessage(eq("MI004"), any(), any())).thenReturn("Subject for account activation email");
        when(templateEngine.process(eq("dadosParaContaMME"), any(Context.class))).thenReturn("Activation email body");

        // Executar o método
        UserDTO dto = new UserDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("sampleToken");
        dto.setNome("test dsads");
        emailService.enviarEmailComSenha(dto);

    }


}
