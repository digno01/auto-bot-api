package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.enums.TipoRendimento;
import br.com.auto.bot.auth.model.Rendimento;
import br.com.auto.bot.auth.repository.RendimentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
@Slf4j
public class RendimentoController {

    @Autowired
    private RendimentoRepository rendimentoRepository;

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Rendimento>> getRendimentosUsuario(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim) {

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Rendimento> rendimentos = rendimentoRepository
                .findRendimentosPeriodo(userId, inicio, fim);

        return ResponseEntity.ok(rendimentos);
    }

    @GetMapping("/resumo/{userId}")
    public ResponseEntity<Map<String, BigDecimal>> getResumoRendimentos(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFim) {

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
