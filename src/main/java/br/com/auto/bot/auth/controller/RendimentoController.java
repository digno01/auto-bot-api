package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.RendimentoDTO;
import br.com.auto.bot.auth.enums.TipoRendimento;
import br.com.auto.bot.auth.exceptions.BussinessException;
import br.com.auto.bot.auth.mapper.RendimentoMapper;
import br.com.auto.bot.auth.model.Rendimento;
import br.com.auto.bot.auth.repository.RendimentoRepository;
import br.com.auto.bot.auth.service.RendimentoService;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rendimentos")
@Tag(name = "Rendimentos", description = "Endpoints para gerenciar rendimentos dos usuários.")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class RendimentoController {

    @Autowired
    private RendimentoRepository rendimentoRepository;

    @Autowired
    private RendimentoService rendimentoService;
    private final RendimentoMapper rendimentoMapper;

    public RendimentoController(RendimentoMapper rendimentoMapper) {
        this.rendimentoMapper = rendimentoMapper;
    }

    @Operation(summary = "Obter rendimentos do investimento por período",
            description = "Retorna uma lista de rendimentos de um investimento específico dentro de um período em dias.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rendimentos retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Período em dias inválido (deve ser entre 10 e 500)."),
            @ApiResponse(responseCode = "404", description = "Investimento não encontrado.")
    })
    @GetMapping("/investimento/{investimentoId}/periodo")
    public ResponseEntity<?> getRendimentosInvestimentoPorPeriodo(
            @Parameter(description = "ID do investimento", required = true)
            @PathVariable Long investimentoId,

            @Parameter(description = "Período em dias (entre 10 e 500)", example = "30")
            @RequestParam(defaultValue = "10") Integer dias) {

        try {
            List<RendimentoDTO> rendimentos = rendimentoService.buscarRendimentosPorPeriodo(investimentoId, dias);

            Map<String, Object> response = new HashMap<>();
            response.put("rendimentos", rendimentos);
            response.put("totalRegistros", rendimentos.size());
            response.put("investimentoId", investimentoId);
            response.put("periodoEmDias", dias);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);

        } catch (BussinessException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    @Operation(summary = "Obter rendimentos do usuário", description = "Retorna uma lista de rendimentos do usuário dentro de um período específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rendimentos retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na validação das datas fornecidas.")
    })
    @GetMapping("/usuario")
    public ResponseEntity<List<RendimentoDTO>> getRendimentosUsuario(
            @Parameter(description = "Data de início do período para consulta", example = "2023-01-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @Parameter(description = "Data de fim do período para consulta", example = "2023-01-31")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim) {

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        Long userId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        List<Rendimento> rendimentos = rendimentoRepository
                .findRendimentosPeriodo(userId, inicio, fim);

        List<RendimentoDTO> rendimentosDTO = rendimentoMapper.toDtoList(rendimentos);

        return ResponseEntity.ok(rendimentosDTO);
    }


    @Operation(summary = "Obter resumo dos rendimentos", description = "Retorna um resumo dos rendimentos do usuário dentro de um período específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo de rendimentos retornado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na validação das datas fornecidas.")
    })
    @GetMapping("/resumo")
    public ResponseEntity<Map<String, BigDecimal>> getResumoRendimentos(
            @Parameter(description = "Data de início do período para consulta", example = "2023-01-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @Parameter(description = "Data de fim do período para consulta", example = "2023-01-31")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim) {

        Long userId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Rendimento> rendimentos = rendimentoRepository
                .findRendimentosPeriodo(userId, inicio, fim);

        Map<String, BigDecimal> resumo = new HashMap<>();

        BigDecimal totalRendimentos = rendimentos.stream()
                .map(Rendimento::getValorRendimento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInvestimentos = rendimentos.stream()
                .filter(r -> TipoRendimento.I.equals(r.getTipoRendimento()))
                .map(Rendimento::getValorRendimento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIndicacoes = rendimentos.stream()
                .filter(r -> isRendimentoIndicacao(r.getTipoRendimento()))
                .map(Rendimento::getValorRendimento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculando rendimentos por nível de indicação
        Map<TipoRendimento, BigDecimal> rendimentosPorNivel = rendimentos.stream()
                .filter(r -> isRendimentoIndicacao(r.getTipoRendimento()))
                .collect(Collectors.groupingBy(
                        Rendimento::getTipoRendimento,
                        Collectors.reducing(BigDecimal.ZERO,
                                Rendimento::getValorRendimento,
                                BigDecimal::add)
                ));

        resumo.put("totalRendimentos", totalRendimentos);
        resumo.put("rendimentosInvestimentos", totalInvestimentos);
        resumo.put("rendimentosIndicacoes", totalIndicacoes);

        // Adicionando rendimentos por nível
        rendimentosPorNivel.forEach((tipo, valor) ->
                resumo.put("rendimentosNivel" + tipo.name().substring(1), valor)
        );

        return ResponseEntity.ok(resumo);
    }

    private boolean isRendimentoIndicacao(TipoRendimento tipo) {
        return tipo == TipoRendimento.N1 ||
                tipo == TipoRendimento.N2 ||
                tipo == TipoRendimento.N3;
    }
}
