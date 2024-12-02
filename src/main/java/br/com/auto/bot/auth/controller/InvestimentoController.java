package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.InvestimentoRequestDTO;
import br.com.auto.bot.auth.dto.InvestimentoResponseDTO;
import br.com.auto.bot.auth.dto.InvestimentoSaqueDTO;
import br.com.auto.bot.auth.service.InvestimentoService;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Investimentos", description = "Endpoints para gerenciar investimentos.")
@RestController
@RequestMapping("/api/investimentos")
@Slf4j
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class InvestimentoController {

    @Autowired
    private InvestimentoService investimentoService;

//    @Operation(summary = "Realizar investimento", description = "Processa um novo investimento.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Investimento realizado com sucesso."),
//            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados do investimento."),
//            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
//    })
//    @PostMapping
//    public ResponseEntity<InvestimentoResponseDTO> realizarInvestimento(
//            @RequestBody @Valid InvestimentoRequestDTO request) {
//        return ResponseEntity.ok(investimentoService.processarInvestimento(request));
//    }

    @Operation(summary = "Buscar investimentos ativos", description = "Retorna uma lista de investimentos ativos de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de investimentos ativos retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping("/ativos/usuario")
    public ResponseEntity<List<InvestimentoResponseDTO>> buscarInvestimentosAtivos() {
        Long usuarioId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        return ResponseEntity.ok(investimentoService.buscarInvestimentosAtivos(usuarioId));
    }

    @Operation(summary = "Calcular saldo para saque", description = "Calcula o saldo disponível para saque do usuário logado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo para saque calculado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping("/saldo-para-saque")
    public ResponseEntity<BigDecimal> calcularSaldoParaSaque() {
        Long userId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        BigDecimal saldoParaSaque = investimentoService.calcularSaldoParaSaque(userId);
        return ResponseEntity.ok(saldoParaSaque);
    }

    @Operation(summary = "Listar investimentos disponíveis para saque", description = "Retorna uma lista de investimentos que têm saldo disponível para saque.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de investimentos para saque retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping("/disponiveis-para-saque")
    public ResponseEntity<List<InvestimentoSaqueDTO>> listarInvestimentosParaSaque() {
        Long usuarioId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        List<InvestimentoSaqueDTO> investimentosParaSaque = investimentoService.listarInvestimentosParaSaque(usuarioId);
        return ResponseEntity.ok(investimentosParaSaque);
    }

    @GetMapping("/verificar-ativo")
    public ResponseEntity<Map<String, Object>> verificarInvestimentoAtivo(
            @RequestParam Long roboId) {

        boolean possuiInvestimentoAtivo = investimentoService.verificarInvestimentoAtivo(ObterDadosUsuarioLogado.getUsuarioLogadoId(), roboId);

        Map<String, Object> response = new HashMap<>();
        response.put("possuiInvestimentoAtivo", possuiInvestimentoAtivo);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar-pagamento-pix")
    @Operation(summary = "Verificar transação ativa", description = "Verifica se uma transação específica está ativa para um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada.")
    })
    public ResponseEntity<Map<String, Object>> verificarTransacaoAtiva(
            @RequestParam BigDecimal idTransacao) {

        boolean transacaoAtiva = investimentoService.verificarPagamentoPixInvestimento(idTransacao, ObterDadosUsuarioLogado.getUsuarioLogadoId());

        Map<String, Object> response = new HashMap<>();
        response.put("transacaoAtiva", transacaoAtiva);

        return ResponseEntity.ok(response);
    }

}
