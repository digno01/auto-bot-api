package br.gov.mme.auth.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHandleDTO {
    private String campo;
    private String message;

    public ErrorHandleDTO(String message) {
        this.message = message;
    }

}
