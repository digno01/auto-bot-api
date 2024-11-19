package br.com.auto.bot.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDTO {

    @NotNull
    @NotEmpty
    private String token;

    @NotNull
    @NotEmpty
    @Schema(description = "senha", example = "senha!213")
    private String newPassword;

}
