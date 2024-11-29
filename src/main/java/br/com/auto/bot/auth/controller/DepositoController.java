package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.DepositoRequestDTO;
import br.com.auto.bot.auth.dto.DepositoResponseDTO;
import br.com.auto.bot.auth.service.DepositoInvestimentoService;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/depositos")
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class DepositoController {

    @Autowired
    private DepositoInvestimentoService depositoService;

    @PostMapping
    public ResponseEntity<DepositoResponseDTO> realizarDeposito(
            @RequestBody @Valid DepositoRequestDTO request) {
        Long userId = ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getId();
        return ResponseEntity.ok(
                depositoService.processarDepositoEInvestimento(request, userId)
        );
    }
}
