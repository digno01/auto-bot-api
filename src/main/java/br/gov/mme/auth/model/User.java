package br.gov.mme.auth.model;

import br.gov.mme.auth.generic.interfaces.IActiveTable;
import br.gov.mme.auth.generic.interfaces.IDeletedTable;
import br.gov.mme.auth.model.permissoes.PerfilAcesso;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "TB_USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements IDeletedTable, IActiveTable, Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_USUARIO", updatable = false, nullable = false)
    private Long id;

    @Column(name = "NU_CPF")
    private String cpf;

    @Column(name = "DS_EMAIL")
    private String email;

    @Column(name = "NO_USUARIO")
    private String nome;

    @JsonProperty("senha")
    @Column(name = "DS_SENHA")
    private String password;

    @Column(name = "NU_DELETED")
    private Boolean isDeleted = false;

    @Column(name = "DT_CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @Column(name = "DT_UPDATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @Column(name = "DS_TOKEN")
    private String token;

    @Column(name = "DS_REFRESH_TOKEN")
    private String refreshToken;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "QT_TENTATIVAS_RECUPERAR_SENHA")
    private Integer quantidadeTentativasRecupSenha = 0;

    @Column(name = "DT_ULTIMA_TENTATIVA_RECUPERACAO")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dtUltimaTentativaRecupSenha;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Contact> contato =  new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "TB_USUARIO_PERFIL", joinColumns = @JoinColumn(name = "PK_USUARIO"), inverseJoinColumns = @JoinColumn(name = "PK_PERFIL_ACESSO"))
    private Set<PerfilAcesso> perfilAcesso = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perfilAcesso.stream()
                .map(perfil -> "ROLE_" + perfil.getPerfil())  // Adiciona o prefixo ROLE_
                .map(SimpleGrantedAuthority::new)  // Cria uma instância de SimpleGrantedAuthority
                .collect(Collectors.toList());  // Coleta em uma lista
    }

    @Override
    public String getUsername() {
        return email;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        setIsDeleted(false);
        setIsActive(true);


    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
