package br.com.auto.bot.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_NIVEL_INDICACAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelIndicacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_NIVEL_INDICACAO")
    private Long id;

    @Column(name = "NU_NIVEL")
    private Integer nivel;

    @Column(name = "NU_PERCENTUAL_RENDIMENTO")
    private BigDecimal percentualRendimento;

    @Column(name = "DS_DESCRICAO")
    private String descricao;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "DT_CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
