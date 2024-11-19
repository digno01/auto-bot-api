package br.gov.mme.auth.controller;

import br.gov.mme.auth.dto.LoginDTO;
import br.gov.mme.auth.dto.MessageDTO;
import br.gov.mme.auth.dto.UpdatePasswordDTO;
import br.gov.mme.auth.dto.UserDTO;
import br.gov.mme.auth.exceptions.BusinessException;
import br.gov.mme.auth.exceptions.RegistroDuplicadoException;
import br.gov.mme.auth.exceptions.RegistroNaoEncontradoException;
import br.gov.mme.auth.model.User;
import br.gov.mme.auth.responses.LoginResponse;
import br.gov.mme.auth.service.JwtService;
import br.gov.mme.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PostMapping("/singup")
    public ResponseEntity create(@Valid @RequestBody UserDTO dto, HttpServletRequest httpServletRequest) throws RegistroDuplicadoException, RegistroNaoEncontradoException {

        // Limpa o CPF antes de prosseguir
        dto.setCpf(dto.getCpf().replaceAll("[.-]", ""));
        serviceBean.validateFields(dto);
        User entity = modelMapper.map(dto, User.class);
        serviceBean.save(entity, dto);

        return ResponseEntity.ok(new MessageDTO("Cadastrado realizado com sucesso."));
    }

    @GetMapping("ativar-conta/{token}")
    public ResponseEntity ativarConta(@PathVariable String token) throws RegistroNaoEncontradoException {
        serviceBean.ativarConta(token);
        return ResponseEntity.ok(new MessageDTO("Conta Ativada com sucesso."));
    }

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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String refreshToken) {
        User user = serviceBean.findByRefreshToken(refreshToken);
        user.setRefreshToken(null);
        serviceBean.save(user);

        return ResponseEntity.ok("Logout realizado com sucesso.");
    }


    @GetMapping(path = "/recovery-password", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity recoveryPassword(@RequestParam(value = "email",required = true) String email, HttpServletRequest request) throws RegistroNaoEncontradoException, BusinessException {
        String ipAddress = getClientIp(request); // Obtém o IP do cliente
        serviceBean.recoverPassword(email, ipAddress);
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
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