package br.gov.mme.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "TB_USUARIO_PERFIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioPerfil {

    @Id
    private Long id;

    @Column(name = "PK_USUARIO")
    private Long idUsuario;

    @Column(name = "PK_PERFIL_ACESSO")
    private Long idPerfil;


}
