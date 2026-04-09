package com.ptut.backend.controller;

import com.ptut.backend.dto.StatistiquesResponse;
import com.ptut.backend.service.StatistiquesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistiques")
public class StatistiquesController {

    private final StatistiquesService statistiquesService;

    public StatistiquesController(StatistiquesService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    @Operation(summary = "Obtenir les statistiques globales (ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<StatistiquesResponse> getStatistiques() {
        return ResponseEntity.ok(statistiquesService.calculer());
    }
}
