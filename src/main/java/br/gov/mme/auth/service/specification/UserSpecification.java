package br.gov.mme.auth.service.specification;

import br.gov.mme.auth.dto.filtro.FiltroLoginDTO;
import br.gov.mme.auth.model.User;
import br.gov.mme.auth.model.permissoes.PerfilAcesso;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class UserSpecification {

    private UserSpecification() {

    }

    public static Specification<User> searchPorPerfilAdmin(FiltroLoginDTO filtro) {
        return nome(filtro.getNome())
                .and(perfil(filtro.getPerfil()))
                .and(hasDeletedFalse());
    }


    protected static Specification<User> nome(String nome) {
        return (root, query, builder) -> Optional.ofNullable(nome)
                .map(f -> builder.like(builder.upper(root.get("nome")),
                        builder.upper(builder.literal("%" + nome + "%"))))
                .orElse(null);
    }


    protected static Specification<User> hasDeletedFalse() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isDeleted"), Boolean.FALSE);
    }

    protected static Specification<User> perfil(String perfil) {
        return (root, query, builder) -> Optional.ofNullable(perfil)
                .map(f -> {
                            Join<User, PerfilAcesso> perfilJoin = root.join("perfilAcesso");
                            return builder.equal(perfilJoin.get("perfil"), perfil);
                        }
                ).orElse(null);
    }

}
