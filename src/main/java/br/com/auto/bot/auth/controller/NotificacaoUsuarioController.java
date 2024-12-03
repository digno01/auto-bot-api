package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.NotificacaoUsuarioDTO;
import br.com.auto.bot.auth.enums.TipoNotificacao;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.service.NotificacaoUsuarioService;
import br.com.auto.bot.auth.util.CustomPageable;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Tag(name = "Notificações", description = "Endpoints para gerenciar notificacoes .")
@RestController
@Slf4j
@RequestMapping("/api/notificacoes")
public class NotificacaoUsuarioController {

    @Autowired
    private NotificacaoUsuarioService service;

    @GetMapping
    public ResponseEntity<Page<NotificacaoUsuarioDTO>> listarNotificacoes(

            @RequestParam(required = false) Boolean lida,
            @RequestParam(required = false) TipoNotificacao tipo,
            CustomPageable pageRequest) {

        Page<NotificacaoUsuarioDTO> notificacoes = service.listarNotificacoes(
                ObterDadosUsuarioLogado.getUsuarioLogadoId(), lida, tipo, pageRequest.toPageable());
        return ResponseEntity.ok(notificacoes);
    }

    @PatchMapping("/{id}/ler")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        service.marcarComoLida(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nao-lidas/contador")
    public ResponseEntity<Long> contarNaoLidas() {
        long quantidade = service.contarNotificacoesNaoLidas(ObterDadosUsuarioLogado.getUsuarioLogadoId());
        return ResponseEntity.ok(quantidade);
    }

    @GetMapping("/teste")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String teste()throws Exception{
        try{
            return "ok";
        }catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping("/check-authorities")
    public ResponseEntity<Map<String, Object>> checkAuthorities(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("isAuthenticated", authentication.isAuthenticated());
        return ResponseEntity.ok(response);
    }
}