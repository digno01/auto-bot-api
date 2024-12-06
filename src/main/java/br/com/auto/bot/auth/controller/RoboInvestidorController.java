package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.service.RoboInvestidorService;
import br.com.auto.bot.auth.util.CustomPageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/robo-investidor")
@Tag(name = "Robô Investidor", description = "Endpoints para gerenciar robôs de investimento automatizado.")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class RoboInvestidorController {

    @Autowired
    private RoboInvestidorService service;

    @Operation(summary = "Listar robôs investidores",
            description = "Retorna uma lista paginada de todos os robôs investidores cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<Page<RoboInvestidor>> findAll(CustomPageable pageRequest) {
        Page<RoboInvestidor> page = service.findAll(pageRequest.toPageable());
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Listar robôs investidores",
            description = "Retorna uma lista paginada de todos os robôs investidores cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/for-user")
    public ResponseEntity<List<RoboInvestidor>> findRoboUserPodeInvestir() {
        return ResponseEntity.ok(service.getRobosInvestidorForUser());
    }

    @Operation(summary = "Buscar robô por ID",
            description = "Retorna um robô investidor específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Robô encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Robô não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoboInvestidor> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }


    @Operation(summary = "Listar robôs investidores por nível",
            description = "Retorna uma lista paginada de robôs investidores filtrados por nível.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nível inválido"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<Page<RoboInvestidor>> findByNivel(
            @PathVariable Integer nivel,
            CustomPageable pageRequest) {
        Page<RoboInvestidor> page = service.findByNivel(nivel, pageRequest.toPageable());
        return ResponseEntity.ok(page);
    }
//    @Operation(summary = "Criar novo robô",
//            description = "Cria um novo robô investidor com as configurações especificadas.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Robô criado com sucesso"),
//            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
//            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
//    })
//    @PostMapping
//    public ResponseEntity<RoboInvestidor> create(@RequestBody RoboInvestidor roboInvestidor) {
//        return ResponseEntity.ok(service.save(roboInvestidor));
//    }

    /*@Operation(summary = "Atualizar robô",
            description = "Atualiza as configurações de um robô investidor existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Robô atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Robô não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoboInvestidor> update(@PathVariable Long id,
                                                 @RequestBody RoboInvestidor roboInvestidor) {
        return ResponseEntity.ok(service.update(id, roboInvestidor));
    }

    @Operation(summary = "Excluir robô",
            description = "Remove um robô investidor do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Robô excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Robô não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }*/
}