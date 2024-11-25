package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.PerfilAcessoDTO;
import br.com.auto.bot.auth.generic.GenericController;
import br.com.auto.bot.auth.model.permissoes.PerfilAcesso;
import br.com.auto.bot.auth.service.PerfilAcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.url}/perfil-acesso")
@Tag(name = "Perfil", description = "Este controlador fornece operações para obter perfis pré-cadastrados no sistema.")
public class PerfilAcessoController extends GenericController<PerfilAcesso, PerfilAcessoDTO, Long> {

    private final PerfilAcessoService service;

    public PerfilAcessoController(PerfilAcessoService service) {
        super(service);
        this.service = service;
    }

    @Operation(summary = "Obter todos os perfis de acesso", description = "Retorna uma lista de todos os perfis de acesso cadastrados no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de perfis de acesso retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Nenhum perfil de acesso encontrado.")
    })
    @GetMapping("/all")
    public ResponseEntity<List<PerfilAcessoDTO>> getAll(HttpServletRequest httpServletRequest) {
        List<PerfilAcesso> entities = this.service.findAll();
        List<PerfilAcessoDTO> tos = entities.stream().map(this::toTO).collect(Collectors.toList());
        return ResponseEntity.ok(tos);
    }

    @Override
    protected PerfilAcesso toEntity(PerfilAcessoDTO dto) {
        return modelMapper.map(dto, PerfilAcesso.class);
    }

    @Override
    protected PerfilAcessoDTO toTO(PerfilAcesso ent) {
        return modelMapper.map(ent, PerfilAcessoDTO.class);
    }
}
