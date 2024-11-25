package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.LoginDTO;
import br.com.auto.bot.auth.dto.MessageDTO;
import br.com.auto.bot.auth.dto.UpdatePasswordDTO;
import br.com.auto.bot.auth.exceptions.BussinessException;
import br.com.auto.bot.auth.exceptions.RegistroDuplicadoException;
import br.com.auto.bot.auth.exceptions.RegistroNaoEncontradoException;
import br.com.auto.bot.auth.responses.LoginResponse;
import br.com.auto.bot.auth.service.JwtService;
import br.com.auto.bot.auth.dto.UserDTO;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de usuários.")
@RequestMapping("${app.api.url}/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final UserService serviceBean;
    @Autowired
    protected ModelMapper modelMapper;


    public AuthenticationController(JwtService jwtService, UserService serviceBean) {
        this.jwtService = jwtService;
        this.serviceBean = serviceBean;
    }

    @Operation(summary = "Cadastrar um novo usuário", description = "Realiza o cadastro de um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastrado realizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos dados.")
    })
    @PostMapping("/singup")
    public ResponseEntity create(@Valid @RequestBody UserDTO dto, HttpServletRequest httpServletRequest)
            throws RegistroDuplicadoException, RegistroNaoEncontradoException {

        // Limpa o CPF antes de prosseguir
        dto.setCpf(dto.getCpf().replaceAll("[.-]", ""));
        serviceBean.validateFields(dto);
        User entity = modelMapper.map(dto, User.class);
        User savedUser = serviceBean.save(entity, dto);

        return ResponseEntity.ok(new MessageDTO("Cadastrado realizado com sucesso."));
    }

    @Operation(summary = "Ativar conta", description = "Ativa a conta de um usuário utilizando um token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta ativada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado.")
    })
    @GetMapping("ativar-conta/{token}")
    public ResponseEntity ativarConta(@PathVariable String token) throws RegistroNaoEncontradoException {
        serviceBean.ativarConta(token);
        return ResponseEntity.ok(new MessageDTO("Conta Ativada com sucesso."));
    }

    @Operation(summary = "Login do usuário", description = "Realiza o login de um usuário e retorna um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDTO loginUserDto, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        User authenticatedUser = serviceBean.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser); // Método que você precisa implementar.

        // Salvar o refresh token no usuário (ou onde você estiver armazenando)
        authenticatedUser.setRefreshToken(refreshToken);
        serviceBean.save(authenticatedUser); // Salva o refresh token no banco

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }


    @Operation(summary = "Atualizar o token de acesso", description = "Atualiza o token JWT usando o refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido.")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody String refreshToken, HttpServletRequest httpServletRequest) {
        // Verifique se o refresh token é válido e não expirou
        User user = serviceBean.findByRefreshToken(refreshToken);

        String newJwtToken = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse()
                .setToken(newJwtToken)
                .setRefreshToken(refreshToken) // Retorne o mesmo refresh token
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);

    }

    @Operation(summary = "Logout do usuário", description = "Realiza o logout do usuário, invalidando o refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String refreshToken) {
        User user = serviceBean.findByRefreshToken(refreshToken);
        user.setRefreshToken(null);
        serviceBean.save(user);

        return ResponseEntity.ok("Logout realizado com sucesso.");
    }

    @Operation(summary = "Recuperar senha", description = "Envia um link para recuperação de senha para o e-mail informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "E-mail enviado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })

    @GetMapping(path = "/recovery-password", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity recoveryPassword(@RequestParam(value = "email",required = true) String email, HttpServletRequest request) throws RegistroNaoEncontradoException, BussinessException {
        String ipAddress = getClientIp(request); // Obtém o IP do cliente
        serviceBean.recoverPassword(email, ipAddress);
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na atualização da senha.")
    })
    @PutMapping(path = "/update-recovery-password", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> atualizarSenha(@RequestBody UpdatePasswordDTO updatePasswordDTO, HttpServletRequest request) {
        serviceBean.atualizarSenha(updatePasswordDTO, getClientIp(request));
        return ResponseEntity.ok("Sua senha foi redefinida com sucesso.");
    }


    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}