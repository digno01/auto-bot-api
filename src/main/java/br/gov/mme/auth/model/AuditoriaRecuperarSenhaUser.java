package br.gov.mme.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_AUD_RECUPERA_SENHA_USUARIO")
public class AuditoriaRecuperarSenhaUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_AUD_RECUPERA_SENHA_USUARIO")
    private Long id;

    @Column(name = "USUARIO_ID")
    private Long userId;

    @Column(name = "DT_CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @Column(name = "SUCCESSO_RECUPERAR")
    private String sucessoRecuperar;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
