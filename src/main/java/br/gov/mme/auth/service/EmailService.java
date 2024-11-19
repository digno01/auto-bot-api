package br.gov.mme.auth.service;

import br.gov.mme.auth.dto.EmailUtil;
import br.gov.mme.auth.dto.UserDTO;
import br.gov.mme.auth.model.User;
import br.gov.mme.auth.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
@Service
public class EmailService {

    private final MessageSource messageSource;
    @Value("${mme.recovery.password}")
    private String urlSistema;

    @Value("${mme.ativacao.conta}")
    private String urlAtivarConta;
    private final TemplateEngine templateEngine;


    @Autowired
    public EmailService(MessageSource messageSource,
                        TemplateEngine templateEngine) {
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }


    public void sendRecoveryEmail(User user) {
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.addAddressTO(user.getEmail());
        emailUtil.setSubject(messageSource.getMessage("MI001", null, null));

        Context context = new Context(Util.LOCALE_BR);
        context.setVariable("nome", user.getNome());

        String url = urlSistema + user.getToken();
        context.setVariable("activationUrl", url);

        String body = this.templateEngine.process("mailPasswordRecovery", context);
        emailUtil.setBody(body).send();

    }


    public void enviaEmailSenhaALteradaSucesso(User user) {
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.addAddressTO(user.getEmail());
        emailUtil.setSubject(messageSource.getMessage("MI002", null, null));

        Context context = new Context(Util.LOCALE_BR);
        context.setVariable("nome", user.getNome());

        String url = urlSistema + user.getToken();
        context.setVariable("activationUrl", url);

        String body = this.templateEngine.process("senhaAlteradaSucesso", context);
        emailUtil.setBody(body).send();

    }


    public void envioEmailAtivacaoConta(User user) {
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.addAddressTO(user.getEmail());
        emailUtil.setSubject(messageSource.getMessage("MI003", null, null));

        Context context = new Context(Util.LOCALE_BR);
        context.setVariable("nome", user.getNome());

        String url = urlAtivarConta + user.getToken();
        context.setVariable("activationUrl", url);

        String body = this.templateEngine.process("ativacaoContaEmail", context);
        emailUtil.setBody(body).send();
    }

    public void enviarEmailComSenha(UserDTO user) {
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.addAddressTO(user.getEmail());
        emailUtil.setSubject(messageSource.getMessage("MI004", null, null));

        Context context = new Context(Util.LOCALE_BR);
        context.setVariable("nome", user.getNome());
        context.setVariable("usuario", user.getEmail());
        context.setVariable("senha", user.getPassword());

        String body = this.templateEngine.process("dadosParaContaMME", context);
        emailUtil.setBody(body).send();
    }
}
