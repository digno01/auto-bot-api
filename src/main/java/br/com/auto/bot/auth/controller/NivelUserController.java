package br.com.auto.bot.auth.controller;


import br.com.auto.bot.auth.dto.*;
import br.com.auto.bot.auth.service.InvestimentoService;
import br.com.auto.bot.auth.service.NivelProgressService;
import br.com.auto.bot.auth.service.NivelService;
import br.com.auto.bot.auth.service.ReferralAnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Nivel Usuário ", description = "Gerenciador Nivel usuário")
@RestController
@RequestMapping("/api/nivel")
@Slf4j
@RequiredArgsConstructor
public class NivelUserController {

//    @Autowired
    private final NivelService nivelService;

    private final ReferralAnalyticsService referralAnalyticsService;

//    @Autowired
    private final NivelProgressService nivelProgressService;

    @GetMapping("/indications")
    public ResponseEntity<List<ReferralAnalyticsDTO>> getReferralAnalytics() {
        List<ReferralAnalyticsDTO> analytics = referralAnalyticsService.getReferralAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/check")
    public ResponseEntity<ProgressoNiveisDTO> nivelUsuario() {
        return ResponseEntity.ok(nivelProgressService.calcularProgressoTodosNiveis());
    }
}
