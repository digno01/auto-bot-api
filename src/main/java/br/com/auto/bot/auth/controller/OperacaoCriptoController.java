package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.model.OperacaoCripto;
import br.com.auto.bot.auth.repository.OperacaoCriptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operacoes")
@RequiredArgsConstructor
public class OperacaoCriptoController {

    private final OperacaoCriptoRepository operacaoCriptoRepository;

    @GetMapping("/rendimento/{rendimentoId}")
    public ResponseEntity<List<OperacaoCripto>> getOperacoesRendimento(
            @PathVariable Long rendimentoId) {
        return ResponseEntity.ok(
                operacaoCriptoRepository.findByRendimentoId(rendimentoId)
        );
    }
}
