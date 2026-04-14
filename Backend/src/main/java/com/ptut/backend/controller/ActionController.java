package com.ptut.backend.controller;

import com.ptut.backend.dto.CreateActionRequest;
import com.ptut.backend.dto.InscriptionResponse;
import com.ptut.backend.entity.Action;
import com.ptut.backend.service.ActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/actions")
public class ActionController {

        private final ActionService actionService;

        public ActionController(ActionService actionService) {
                this.actionService = actionService;
        }

        @Operation(summary = "Lister toutes les actions (ADMIN/AMBASSADEUR)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des actions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Action.class))),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<Action>> listActions() {
                return ResponseEntity.ok(actionService.listAllActions());
        }

        @Operation(summary = "Créer une action (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Action créée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Action.class))),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Action> createAction(@RequestBody CreateActionRequest request) {
                Action createdAction = actionService.createAction(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdAction);
        }

        @Operation(summary = "Modifier une action existante (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Action modifiée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Action.class))),
                        @ApiResponse(responseCode = "404", description = "Action introuvable", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Action> updateAction(@PathVariable("id") Long idAction,
                        @RequestBody CreateActionRequest request) {
                Action updatedAction = actionService.updateAction(idAction, request);
                return ResponseEntity.ok(updatedAction);
        }

        @Operation(summary = "Supprimer une action existante (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Action supprimée", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Action introuvable", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteAction(@PathVariable("id") Long idAction) {
                actionService.deleteAction(idAction);
                return ResponseEntity.ok(Map.of("message", "Action supprimée"));
        }

        @Operation(summary = "S'inscrire à une action (AMBASSADEUR)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Inscription créée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InscriptionResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Action ou utilisateur introuvable", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Déjà inscrit ou capacité atteinte", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(value = "/{id}/inscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<InscriptionResponse> inscrireAction(
                        @PathVariable("id") Long idAction,
                        Authentication authentication) {
                InscriptionResponse response = actionService.inscrireAmbassadeur(idAction, authentication.getName());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Lister les inscriptions d'une action (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des inscriptions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InscriptionResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Action introuvable", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @GetMapping(value = "/{id}/inscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<InscriptionResponse>> listInscriptionsByAction(@PathVariable("id") Long idAction) {
                return ResponseEntity.ok(actionService.listInscriptionsByAction(idAction));
        }

        @Operation(summary = "Lister mes inscriptions (AMBASSADEUR)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste de mes inscriptions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InscriptionResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @GetMapping(value = "/inscriptions/me", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<InscriptionResponse>> listMesInscriptions(Authentication authentication) {
                return ResponseEntity.ok(actionService.listMesInscriptions(authentication.getName()));
        }

        @Operation(summary = "Lister les dossiers en cours de traitement (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des dossiers en cours", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InscriptionResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @GetMapping(value = "/inscriptions/dossiers-en-cours", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<InscriptionResponse>> listDossiersEnCours() {
                return ResponseEntity.ok(actionService.listDossiersEnCours());
        }

        @Operation(summary = "Se désinscrire d'une action (AMBASSADEUR)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Désinscription effectuée", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Action, utilisateur ou inscription introuvable", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @DeleteMapping("/{id}/inscriptions")
        public ResponseEntity<Map<String, String>> desinscrireAction(
                        @PathVariable("id") Long idAction,
                        Authentication authentication) {
                actionService.desinscrireAmbassadeur(idAction, authentication.getName());
                return ResponseEntity.ok(Map.of("message", "Désinscription effectuée"));
        }

        @Operation(summary = "Déposer un justificatif de présence (AMBASSADEUR)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Justificatif déposé", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Fichier invalide", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Action, utilisateur ou inscription introuvable", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(value = "/{id}/justificatif", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Map<String, String>> deposerJustificatifPresence(
                        @PathVariable("id") Long idAction,
                        @RequestParam("file") MultipartFile file,
                        Authentication authentication) {
                actionService.deposerJustificatifPresence(idAction, authentication.getName(), file);
                return ResponseEntity.ok(Map.of("message", "Justificatif déposé"));
        }

        @Operation(summary = "Valider un dossier ambassadeur (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Dossier validé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InscriptionResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Inscription introuvable", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Dossier déjà traité ou justificatif absent", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping(value = "/{id}/inscriptions/{idInscription}/valider", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<InscriptionResponse> validerDossier(
                        @PathVariable("id") Long idAction,
                        @PathVariable("idInscription") Long idInscription) {
                return ResponseEntity.ok(actionService.validerDossier(idAction, idInscription));
        }

        @Operation(summary = "Refuser un dossier ambassadeur (ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Dossier refusé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InscriptionResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Inscription introuvable", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Dossier déjà traité ou justificatif absent", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
                        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content)
        })
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping(value = "/{id}/inscriptions/{idInscription}/refuser", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<InscriptionResponse> refuserDossier(
                        @PathVariable("id") Long idAction,
                        @PathVariable("idInscription") Long idInscription) {
                return ResponseEntity.ok(actionService.refuserDossier(idAction, idInscription));
        }
}