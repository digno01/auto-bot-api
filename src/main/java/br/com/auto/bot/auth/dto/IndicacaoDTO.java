package br.com.auto.bot.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferência de dados para Indicação")
public class IndicacaoDTO {

    private Long id;
    private Long idUsuario;
    private Long idUsuarioIndicador;
    private Integer nivel;
    private Boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
}