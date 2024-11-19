package br.com.auto.bot.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferência de dados para Login")
public class LoginDTO {

    @Schema(description = "email", example = "teste@mme.gov.com")
    @NotNull
    @NotEmpty
    @Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Endereço de e-mail informado inválido")
    private String email;

    @Schema(description = "senha", example = "senha!213")
//    @NotNull
//    @NotEmpty
    @JsonProperty("senha")
    //@Pattern(regexp="^(?=.*[A-Z])(?=.*[!@#$%&*()-_=+<>?])(?=.*[0-9])(?=.*[a-z]).{6,15}$", message = "Senha inválida")
    private String password;


}
