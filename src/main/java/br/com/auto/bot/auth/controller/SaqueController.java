package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.SaqueRequestDTO;
import br.com.auto.bot.auth.dto.SaqueResponseDTO;
import br.com.auto.bot.auth.model.Saque;
import br.com.auto.bot.auth.service.SaqueService; // Crie um serviço para gerenciar os saques
import br.com.auto.bot.auth.util.CustomPageable;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saques")
@Tag(name = "Saques", description = "Endpoints para gerenciar solicitações de saque.")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class SaqueController {

    @Autowired
    private SaqueService saqueService; // Serviço que gerencia a lógica de saque

    @Operation(summary = "Solicitar saque", description = "Permite ao usuário solicitar um saque de um investimento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saque solicitado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro ao solicitar saque, pode ser devido a saldo insuficiente ou dados inválidos.")
    })
    @PostMapping
    public ResponseEntity<Saque> solicitarSaque(@RequestBody @Valid SaqueRequestDTO request) {
        Long usuarioId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        Saque saque = saqueService.perepararSaque(request, usuarioId);
        return ResponseEntity.ok(saque);
    }

    @Operation(summary = "Listar solicitações de saque", description = "Retorna todas as solicitações de saque feitas pelo usuário.")
    @GetMapping
    public ResponseEntity<List<SaqueResponseDTO>> listarSolicitacoesSaque() {
        Long usuarioId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        List<SaqueResponseDTO> saques = saqueService.listarSolicitacoesSaque(usuarioId);
        return ResponseEntity.ok(saques);
    }



    @Operation(summary = "Listar saques pendentes",
            description = "Retorna uma lista paginada de todos os saques pendentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar este recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/pendentes")
    public ResponseEntity<Page<SaqueResponseDTO>> findAllPendentes(CustomPageable pageRequest) {
        Page<SaqueResponseDTO> page = saqueService.findAllPendentes(pageRequest.toPageable());
        return ResponseEntity.ok(page);
    }
}
