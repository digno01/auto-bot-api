package br.com.auto.bot.auth.model.permissoes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_PERFIL_ACESSO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Access(value=AccessType.FIELD)
public class PerfilAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_PERFIL_ACESSO", updatable = false, nullable = false)
    private Long id;

    @Column(name = "NO_PERFIL_ACESSO")
    private String perfil;

    @Column(name = "DS_PERFIL")
    private String descricao;

    public PerfilAcesso(Long id) {
        this.id = id;
    }
}
