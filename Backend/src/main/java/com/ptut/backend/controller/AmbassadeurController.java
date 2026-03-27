package com.ptut.backend.controller;

import com.ptut.backend.dto.ActionBriefResponse;
import com.ptut.backend.dto.UserSummaryResponse;
import com.ptut.backend.entity.Role;
import com.ptut.backend.entity.Utilisateur;
import com.ptut.backend.repository.InscriptionRepository;
import com.ptut.backend.repository.UtilisateurRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ambassadeurs")
public class AmbassadeurController {

    private final UtilisateurRepository utilisateurRepository;
    private final InscriptionRepository inscriptionRepository;

    public AmbassadeurController(UtilisateurRepository utilisateurRepository, InscriptionRepository inscriptionRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.inscriptionRepository = inscriptionRepository;
    }

    @Transactional(readOnly = true)
    @Operation(summary = "Lister les ambassadeurs")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> listAmbassadeurs() {
        List<UserSummaryResponse> ambassadeurs = utilisateurRepository.findAllByRole(Role.AMBASSADEUR).stream()
                .map(user -> {
                    UserSummaryResponse dto = new UserSummaryResponse(
                            user.getIdUtilisateur(),
                            user.getNom(),
                            user.getPrenom(),
                            user.getEmail(),
                            user.getRole() != null ? user.getRole().name() : null
                    );
                    List<ActionBriefResponse> actions = inscriptionRepository
                            .findByUtilisateur_EmailOrderByDateInscriptionDesc(user.getEmail())
                            .stream()
                            .map(i -> new ActionBriefResponse(
                                    i.getAction().getIdAction(),
                                    i.getAction().getTitre(),
                                    i.getAction().getTypeAction(),
                                    i.getAction().getDateAction(),
                                    i.getStatutInscription()
                            ))
                            .toList();
                    dto.setActions(actions);
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(ambassadeurs);
    }
}
