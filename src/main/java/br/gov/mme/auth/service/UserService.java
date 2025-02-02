package br.gov.mme.auth.service;

import br.gov.mme.auth.dto.LoginDTO;
import br.gov.mme.auth.dto.UpdatePasswordDTO;
import br.gov.mme.auth.dto.UserDTO;
import br.gov.mme.auth.dto.enums.SimNaoEnum;
import br.gov.mme.auth.dto.filtro.FiltroLoginDTO;
import br.gov.mme.auth.exceptions.*;
import br.gov.mme.auth.exceptions.enuns.AuthMessageCode;
import br.gov.mme.auth.generic.service.GenericService;
import br.gov.mme.auth.mapper.UserMapper;
import br.gov.mme.auth.model.AuditoriaRecuperarSenhaUser;
import br.gov.mme.auth.model.Contact;
import br.gov.mme.auth.model.User;
import br.gov.mme.auth.model.error.ErrorHandleDTO;
import br.gov.mme.auth.model.permissoes.PerfilAcesso;
import br.gov.mme.auth.repository.AuditoriaRecuperarSenhaUserRepository;
import br.gov.mme.auth.repository.ContactRepository;
import br.gov.mme.auth.repository.UserRepository;
import br.gov.mme.auth.service.specification.UserSpecification;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService extends GenericService<User, Long> {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final MessageSource messageSource;

    private final AuditoriaRecuperarSenhaUserRepository auditoriaRecuperarSenhaUserRepository;

    @Autowired
    private UserRepository repository;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactService contactService;


    @Autowired
    private PerfilAcessoService perfilAcessoService;

    @Autowired
    private UsuarioPerfilService usuarioPerfilService;

    @Autowired
    public UserService(UserRepository repository,
                       ContactService contactService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       EmailService emailService,
                       AuditoriaRecuperarSenhaUserRepository auditoriaRecuperarSenhaUserRepository,
                       MessageSource messageSource,
                       PerfilAcessoService perfilAcessoService,
                       UsuarioPerfilService usuarioPerfilService) {
        super();
        this.repository = repository;
        this.contactService = contactService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.auditoriaRecuperarSenhaUserRepository = auditoriaRecuperarSenhaUserRepository;
        this.messageSource = messageSource;
        this.perfilAcessoService = perfilAcessoService;
        this.usuarioPerfilService = usuarioPerfilService;
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<User> findAllByIsDeletedFalseAndPerfilAcesso(Pageable pageable) throws RegistroNaoEncontradoException {
        List<PerfilAcesso> listaPerfilAcessoPorSistema = perfilAcessoService.findAll();
        Page<User> pageUser = this.repository.findAllByIsDeletedFalseAndHasPerfilAcesso(pageable);
        //filtra e mantem apenas os perfils informados pelo sistema vinculado
        removerPerfilsQueNaoSaoDosistemaAssociado(pageUser, listaPerfilAcessoPorSistema);
        return pageUser;
    }

    private static void removerPerfilsQueNaoSaoDosistemaAssociado(Page<User> pageUser, List<PerfilAcesso> listaPerfilAcessoPorSistema) {
        pageUser.getContent().stream().map(e->  {
            Set<PerfilAcesso> perfilAcessoMutable = new HashSet<>(e.getPerfilAcesso());
            perfilAcessoMutable.retainAll(listaPerfilAcessoPorSistema);
            e.setPerfilAcesso(perfilAcessoMutable);
            return e;
        }).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<User> findAllByFiltro(Pageable pageable, FiltroLoginDTO filtro) throws RegistroNaoEncontradoException {
        List<PerfilAcesso> listaPerfilAcessoPorSistema = perfilAcessoService.findAll();
        Page<User> pageUser = this.repository.findAll(UserSpecification.searchPorPerfilAdmin(filtro), pageable);

        //filtra e mantem apenas os perfils informados pelo sistema vinculado
        removerPerfilsQueNaoSaoDosistemaAssociado(pageUser, listaPerfilAcessoPorSistema);

        return pageUser;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public User findById(Long id, String sistema) throws RegistroNaoEncontradoException {
        List<PerfilAcesso> listaPerfilAcessoPorSistema = perfilAcessoService.findAll();
        User userEntity = obterUsuario(id);
        Set<PerfilAcesso> perfilAcessoMutable = new HashSet<>(userEntity.getPerfilAcesso());
        perfilAcessoMutable.retainAll(listaPerfilAcessoPorSistema);
        userEntity.setPerfilAcesso(perfilAcessoMutable);
        return userEntity;
    }

    @Transactional
    public User save(User entity, UserDTO dto) throws RegistroNaoEncontradoException {

        entity.setContato(null);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        String token = jwtService.generateRecoverToken(entity);
        entity.setToken(token);

        //entity.setIsActive(!dto.getIsExterno());

        User user = this.repository.save(entity);
        contactService.saveOrUpdate(dto.getContato(), user);

        if (dto.getIsExterno()) {
            emailService.envioEmailAtivacaoConta(user);
        } else {
            emailService.enviarEmailComSenha(dto);
        }
        return user;
    }



    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private PerfilAcesso buscarPerfil(Long idPerfil) {
        Optional<PerfilAcesso> perfilVinculadoAoSistema = perfilAcessoService.findById( idPerfil);
        if (perfilVinculadoAoSistema.isEmpty()) {
            throw new BusinessException(messageSource.getMessage("ME035", null, null));
        }
        return perfilVinculadoAoSistema.get();
    }

    @Transactional
    public User update(UserDTO dto) throws RegistroNaoEncontradoException {
        User u = obterUsuario(dto.getId());
        User userMapeadoComNovosValores = UserMapper.map(u, dto);
        User user = this.repository.save(userMapeadoComNovosValores);
        contactService.saveOrUpdate(dto.getContato(), user);
        return user;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)

    protected User obterUsuario(Long id) throws RegistroNaoEncontradoException {
        Optional<User> u = Optional.ofNullable(
                repository.findById(id).orElseThrow(
                        () -> new RegistroNaoEncontradoException("Identificador não corresponde a nenhum registro informado não existe")
                )
        );
        // Buscando contatos ativos
        List<Contact> activeContacts = contactRepository.findActiveContactsByUserId(u.get().getId());
        u.get().setContato(activeContacts);
        return u.get();
    }

    public void ativarConta(String token) throws RegistroNaoEncontradoException {
        Optional<User> u = Optional.ofNullable(
                repository.findByToken(token).orElseThrow(
                        () -> new RegistroNaoEncontradoException(messageSource.getMessage("ME036", null, null))
                )
        );
        User user = u.get();
        user.setIsActive(true);
        repository.save(user);
    }


    public User authenticate(LoginDTO input) throws RegistroNaoEncontradoException {
        try {
            authenticateUser(input);
        } catch (Exception e) {
            throw new BusinessException(messageSource.getMessage("ME003", null, null));
        }
        User user = findUserByEmail(input.getEmail());

        if (Boolean.FALSE.equals(isSenhaIgualUserPassword(user, input.getPassword()))) {
            throw new BusinessException(messageSource.getMessage("ME003", null, null));
        }
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BusinessException(messageSource.getMessage("ME034", null, null));
        }
        validateUserForPasswordRecovery(user);

        return user;

    }

    private void authenticateUser(LoginDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
    }

    private User findUserByEmail(String email) throws RegistroNaoEncontradoException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RegistroNaoEncontradoException(
                        messageSource.getMessage("ME003", null, null)
                ));
    }

    protected void validateUserForPasswordRecovery(User user) {
        if (user.getQuantidadeTentativasRecupSenha() > 0 && StringUtils.isNotEmpty(user.getToken())) {
            throw new BusinessException(messageSource.getMessage("ME033", null, null));
        }
    }


    public User findByRefreshToken(String refreshToken) {
        return repository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException(messageSource.getMessage("ME037", null, null)));
    }


    private String cleanCPF(String cpf) {
        return cpf != null ? cpf.replaceAll("[.-]", "") : null;
    }

    public void validateFields(UserDTO dto) throws RegistroDuplicadoException {
        List<ErrorHandleDTO> list = new ArrayList<>();

        String cpfLimpo = cleanCPF(dto.getCpf());
        dto.setCpf(cpfLimpo); // Atualiza o DTO com o CPF limpo

        getListError(list, repository.findByCpf(cpfLimpo) == null ? null : "CPF já cadastrado", "CPF");
        getListError(list, repository.findByEmail(dto.getEmail()).isPresent() ? "E-mail já cadastrado" : null, "E-mail");

        if (!list.isEmpty()) {
            Gson gson = new Gson();
            throw new RegistroDuplicadoException(gson.toJson(list));
        }
    }

    protected void getListError(List<ErrorHandleDTO> list, String msg, String campo) {
        if (msg != null) {
            list.add(new ErrorHandleDTO(campo, msg));
        }
    }


    public void recoverPassword(String email, String ipAddress) throws RegistroNaoEncontradoException, BusinessException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RegistroNaoEncontradoException(messageSource.getMessage("ME031", null, null)));

        controlarNumeroTentativasRecuperarSenha(user);
        // Gera um token com validade de 10 minutos
        user.setDtUltimaTentativaRecupSenha(LocalDateTime.now());
        gerarToken(user);

        // Registrar a tentativa bem-sucedida
        auditarRecuperarSenha(user.getId(), true, ipAddress);

        // Envia o e-mail de recuperação
        emailService.sendRecoveryEmail(user);

    }

    private void gerarToken(User user) {
        String token = jwtService.generateRecoverToken(user); // Usar o método que gera o token
        user.setToken(token);
        repository.save(user);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditarRecuperarSenha(Long userId, boolean success, String ipAddress) {
        AuditoriaRecuperarSenhaUser audit = new AuditoriaRecuperarSenhaUser();
        audit.setUserId(userId);
        audit.setSucessoRecuperar(success ? SimNaoEnum.SIM.getCodigo() : SimNaoEnum.NAO.getCodigo());
        audit.setIpAddress(ipAddress);
        auditoriaRecuperarSenhaUserRepository.save(audit);

    }

    protected void controlarNumeroTentativasRecuperarSenha(User user) throws BusinessException {
        // Verifique se o usuário foi bloqueado
        if (user.getQuantidadeTentativasRecupSenha() >= 3) {
            if (user.getDtUltimaTentativaRecupSenha() != null && user.getDtUltimaTentativaRecupSenha().plusMinutes(10).isAfter(LocalDateTime.now())) {
                throw new BusinessException(messageSource.getMessage("ME030", null, null));
            } else {
                // Resetar tentativas após 10 minutos
                user.setQuantidadeTentativasRecupSenha(1);
            }
        } else {
            user.setQuantidadeTentativasRecupSenha(user.getQuantidadeTentativasRecupSenha() + 1);
        }
    }


    public void atualizarSenha(UpdatePasswordDTO atualizarSenhaDTO, String ipAddress) throws BusinessException {
        String token = atualizarSenhaDTO.getToken();
        String novaSenha = atualizarSenhaDTO.getNewPassword();

        String email = jwtService.extractUsername(token);
        validarEmailDoToken(email);
        User usuario = encontrarUsuarioPorEmail(email);
        try {
            validarTokenAlterarSenha(token, usuario);
            validarTokenERegistrarTentativa(token, usuario, ipAddress);
            if (isSenhaIgualUserPassword(usuario, novaSenha)) {
                throw new BusinessException(messageSource.getMessage(AuthMessageCode.SENHA_DEVE_SER_DIFERENTE_ANTERIOR.getCode(), null, null));
            }
            // Atualiza a senha do usuário e limpa o token
            atualizarSenhaDoUsuario(usuario, novaSenha);
            enviaEmailSenhaALteradaSucesso(usuario);
            // Registrar a tentativa bem-sucedida
            auditarRecuperarSenha(usuario.getId(), true, ipAddress);
        } catch (Exception e) {
            // Registrar a tentativa bem-sucedida
            auditarRecuperarSenha(usuario.getId(), false, ipAddress);
            throw e;
        }
    }

    protected void validarTokenAlterarSenha(String token, User usuario) {
        if (usuario.getToken() == null || !token.equals(usuario.getToken())) {
            throw new BusinessException(messageSource.getMessage(AuthMessageCode.ERROR_TOKEN_INVALID.getCode(), null, null));
        }
    }

    protected Boolean isSenhaIgualUserPassword(User user, String novaSenhaDecript) {
        if (passwordEncoder.matches(novaSenhaDecript, user.getPassword())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void enviaEmailSenhaALteradaSucesso(User usuario) {
        emailService.enviaEmailSenhaALteradaSucesso(usuario);
    }

    private User encontrarUsuarioPorEmail(String email) throws BusinessException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(messageSource.getMessage(AuthMessageCode.ERROR_TOKEN_INVALID.getCode(), null, null)));
    }

    protected void validarTokenERegistrarTentativa(String token, User usuario, String ipAddress) throws BusinessException {
        if (jwtService.isTokenExpired(token)) {
            auditarRecuperarSenha(usuario.getId(), false, ipAddress);
            controlarNumeroTentativasRecuperarSenha(usuario);
            throw new TokenExpiredException("Token expirado");
        }
    }

    protected void atualizarSenhaDoUsuario(User usuario, String novaSenha) {
        usuario.setPassword(passwordEncoder.encode(novaSenha));
        usuario.setToken(null); // Limpa o token após a utilização
        usuario.setQuantidadeTentativasRecupSenha(0); // Reseta as tentativas de recuperação
        repository.save(usuario);
    }

    protected void validarEmailDoToken(String email) throws BusinessException {
        if (email == null) {
            throw new BusinessException((messageSource.getMessage(AuthMessageCode.ERROR_TOKEN_INVALID.getCode(), null, null)));
        }
    }

}