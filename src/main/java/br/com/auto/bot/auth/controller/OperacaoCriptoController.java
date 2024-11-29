package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.model.OperacaoCripto;
import br.com.auto.bot.auth.repository.OperacaoCriptoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Operações Cripto", description = "Endpoints para gerenciar operações de criptomoedas.")
@RestController
@RequestMapping("/api/operacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class OperacaoCriptoController {

    private final OperacaoCriptoRepository operacaoCriptoRepository;

    @Operation(summary = "Obter operações de rendimento", description = "Retorna uma lista de operações de criptomoedas associadas a um rendimento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de operações retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Rendimento não encontrado.")
    })
    @GetMapping("/rendimento/{rendimentoId}")
    public ResponseEntity<List<OperacaoCripto>> getOperacoesRendimento(
            @Parameter(description = "ID do rendimento para buscar operações associadas") @PathVariable Long rendimentoId) {
        return ResponseEntity.ok(
                operacaoCriptoRepository.findByRendimentoId(rendimentoId)
        );
    }
}
