package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.EnumResponseDTO;
import br.com.auto.bot.auth.dto.enums.UnidadeFederativaEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.url.public}/unidade-federativa")
@Tag(name = "Controler orgão instituição", description = "Este controlador fornece operações de exemplo de CRUD para Unidade Federativa.")
public class UnidadeFederativaController {

    @GetMapping
    public ResponseEntity<List<EnumResponseDTO>> find()  {
        return ResponseEntity.ok(Arrays.stream(UnidadeFederativaEnum.values())
                .map(e -> new EnumResponseDTO(e.getSigla(),e.getNome()))
                .collect(Collectors.toList()));
    }

}
