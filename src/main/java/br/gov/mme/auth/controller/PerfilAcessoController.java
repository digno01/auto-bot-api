package br.gov.mme.auth.controller;

import br.gov.mme.auth.dto.PerfilAcessoDTO;
import br.gov.mme.auth.generic.GenericController;
import br.gov.mme.auth.model.permissoes.PerfilAcesso;
import br.gov.mme.auth.service.PerfilAcessoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("${app.api.url}/perfil-acesso")
@Tag(name = "Perfil", description = "Este controlador fornece operações de exemplo de obter perfis pré cadastrado no sistema.")
public class PerfilAcessoController extends GenericController<PerfilAcesso, PerfilAcessoDTO, Long> {

    private final PerfilAcessoService service;

    public PerfilAcessoController(PerfilAcessoService service) {
        super(service);
        this.service = service;
    }

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
