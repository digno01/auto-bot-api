package br.gov.mme.auth.dto;

import br.gov.mme.auth.validation.cpf.IsValidCpf;
import br.gov.mme.auth.validation.uf.IsValidUf;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferência de dados para User")
public class UserDTO extends LoginDTO {

    @Schema(description = "ID", example = "1253")
    private Long id;

    @Schema(description = "cpf", example = "00012649779")
    @IsValidCpf
    private String cpf;

    @NotNull
    @NotEmpty
    private String nome;

    @Schema(description = "Indica se está ativo", example = "true")
    private Boolean isActive;

    @Schema(description = "Indica se está deletado", example = "false")
    private Boolean isDeleted = false;

    @Schema(description = "Data de criação", example = "2024-05-27T02:48:47.625Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização", example = "2024-05-27T02:48:47.625Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @NotEmpty
    private List<ContactDTO> contato;

    private Set<PerfilAcessoDTO> perfilAcesso;

    private String sistema;

//    @NotNull
    private Boolean isExterno;

}
