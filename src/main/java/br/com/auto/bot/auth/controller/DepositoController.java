package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.DepositoRequestDTO;
import br.com.auto.bot.auth.dto.DepositoResponseDTO;
import br.com.auto.bot.auth.service.DepositoInvestimentoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/depositos")
@Slf4j
public class DepositoController {

    @Autowired
    private DepositoInvestimentoService depositoService;

    @PostMapping
    public ResponseEntity<DepositoResponseDTO> realizarDeposito(
            @RequestBody @Valid DepositoRequestDTO request) {
        return ResponseEntity.ok(
                depositoService.processarDepositoEInvestimento(request)
        );
    }
}
