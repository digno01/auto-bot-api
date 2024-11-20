package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.InvestimentoRequestDTO;
import br.com.auto.bot.auth.dto.InvestimentoResponseDTO;
import br.com.auto.bot.auth.service.InvestimentoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investimentos")
@Slf4j
public class InvestimentoController {

    @Autowired
    private InvestimentoService investimentoService;

    @PostMapping
    public ResponseEntity<InvestimentoResponseDTO> realizarInvestimento(
            @RequestBody @Valid InvestimentoRequestDTO request) {
        return ResponseEntity.ok(investimentoService.processarInvestimento(request));
    }

    @GetMapping("/ativos/{usuarioId}")
    public ResponseEntity<List<InvestimentoResponseDTO>> buscarInvestimentosAtivos(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(investimentoService.buscarInvestimentosAtivos(usuarioId));
    }
}
