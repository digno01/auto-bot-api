package br.com.auto.bot.auth.util;

import br.com.auto.bot.auth.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ObterDadosUsuarioLogado {


    public static boolean perfilAdmin() {
            return SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean perfilGestor() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));
    }

    public static boolean perfilSuporte() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TI_SUPORTE"));
    }

    public static boolean pefilParticipante() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARTICIPANTE"));
    }

    public static boolean pefilParticipanteOuDeslogado() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARTICIPANTE") || a.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    public static User obterDadosUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof User) {
            return ((User) principal);
        }

        throw new SecurityException("Usuário não autenticado ou tipo inválido");
    }

    public static Long getUsuarioLogadoId() {
        return obterDadosUsuarioLogado().getId();
    }

    public static boolean isUsuarioLogado(Long userId) {
        return getUsuarioLogadoId().equals(userId);
    }
}
