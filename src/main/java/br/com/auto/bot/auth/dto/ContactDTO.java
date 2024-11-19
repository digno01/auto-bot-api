package br.com.auto.bot.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {

    private Integer id;

    @NotNull
    @NotEmpty
    @Size(max = 2, min = 2)
    private Integer ddd;

    @NotNull
    @NotEmpty
    @Size(max = 9, min = 8)
    private String numero;

    @NotNull
    @NotEmpty
    @Size(max=1, min = 0)
    private Integer tipo;

    @NotNull
    private boolean remove;

    private Boolean isActive;

    private Boolean isDeleted;
}
