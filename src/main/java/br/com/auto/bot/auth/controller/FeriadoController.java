package br.com.auto.bot.auth.controller;


import br.com.auto.bot.auth.dto.FeriadoResponse;
import br.com.auto.bot.auth.service.FeriadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feriado")
public class FeriadoController {

    private final FeriadoService feriadoService;

    public FeriadoController(FeriadoService feriadoService) {
        this.feriadoService = feriadoService;
    }

    @GetMapping("/check")
    public ResponseEntity<FeriadoResponse> checkCurrentDay() {
        return ResponseEntity.ok(feriadoService.checkCurrentDate());
    }
}