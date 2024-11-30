package br.com.auto.bot.auth.model;

import br.com.auto.bot.auth.generic.interfaces.IDeletedTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "TB_CONTATO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact implements IDeletedTable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_CONTATO", updatable = false, nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="PK_USUARIO")
    private User user;


    @Column(name = "NU_DDD")
    private Integer ddd;

    @Column(name = "NU_TELEFONE")
    private String numero;

    @Column(name = "TP_CONTATO")
    private Integer tipo;

    @Column(name = "NU_DELETED")
    private Boolean isDeleted = false;

    @Column(name = "DT_CREATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @Column(name = "DT_UPDATED_AT")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        setIsDeleted(false);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
