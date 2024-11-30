package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.EnumResponseDTO;
import br.com.auto.bot.auth.dto.enums.UnidadeFederativaEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.url.public}/unidade-federativa")
@Tag(name = "Unidade Federativa", description = "Este controlador fornece operações para obter unidades federativas.")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UnidadeFederativaController {

    @Operation(summary = "Listar unidades federativas", description = "Retorna uma lista de todas as unidades federativas disponíveis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de unidades federativas retornada com sucesso.")
    })
    @GetMapping
    public ResponseEntity<List<EnumResponseDTO>> find() {
        List<EnumResponseDTO> unidadesFederativas = Arrays.stream(UnidadeFederativaEnum.values())
                .map(e -> new EnumResponseDTO(e.getSigla(), e.getNome()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(unidadesFederativas);
    }
}
