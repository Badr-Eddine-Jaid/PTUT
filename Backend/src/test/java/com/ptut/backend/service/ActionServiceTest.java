package com.ptut.backend.service;

import com.ptut.backend.dto.InscriptionResponse;
import com.ptut.backend.entity.Action;
import com.ptut.backend.entity.DocumentResource;
import com.ptut.backend.entity.Inscription;
import com.ptut.backend.entity.Role;
import com.ptut.backend.entity.Utilisateur;
import com.ptut.backend.repository.ActionRepository;
import com.ptut.backend.repository.DocumentResourceRepository;
import com.ptut.backend.repository.InscriptionRepository;
import com.ptut.backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private InscriptionRepository inscriptionRepository;
    @Mock
    private DocumentResourceService documentResourceService;
    @Mock
    private DocumentResourceRepository documentResourceRepository;

    @InjectMocks
    private ActionService actionService;

    private Utilisateur ambassadeur;
    private Action action;

    @BeforeEach
    void setUp() {
        ambassadeur = new Utilisateur();
        ambassadeur.setIdUtilisateur(1L);
        ambassadeur.setEmail("amb@test.com");
        ambassadeur.setNom("Dupont");
        ambassadeur.setPrenom("Jean");
        ambassadeur.setRole(Role.AMBASSADEUR);

        action = new Action();
        action.setIdAction(10L);
        action.setTitre("Forum des métiers");
        action.setCapaciteMax(null);
    }



    @Test
    void inscrireAmbassadeur_OK() {
        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));
        when(actionRepository.findById(10L)).thenReturn(Optional.of(action));
        when(inscriptionRepository.existsByActionAndUtilisateur(action, ambassadeur)).thenReturn(false);

        Inscription saved = new Inscription();
        saved.setIdInscription(1L);
        saved.setAction(action);
        saved.setUtilisateur(ambassadeur);
        saved.setDateInscription(Instant.now());
        saved.setStatutInscription("INSCRIT");
        when(inscriptionRepository.save(any())).thenReturn(saved);

        InscriptionResponse response = actionService.inscrireAmbassadeur(10L, "amb@test.com");

        assertThat(response.getStatutInscription()).isEqualTo("INSCRIT");
        assertThat(response.getIdAction()).isEqualTo(10L);
        verify(inscriptionRepository).save(any(Inscription.class));
    }

    @Test
    void inscrireAmbassadeur_dejaInscrit_throwsConflict() {
        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));
        when(actionRepository.findById(10L)).thenReturn(Optional.of(action));
        when(inscriptionRepository.existsByActionAndUtilisateur(action, ambassadeur)).thenReturn(true);

        assertThatThrownBy(() -> actionService.inscrireAmbassadeur(10L, "amb@test.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Déjà inscrit");
    }

    @Test
    void inscrireAmbassadeur_capaciteMax_throwsConflict() {
        action.setCapaciteMax(2);
        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));
        when(actionRepository.findById(10L)).thenReturn(Optional.of(action));
        when(inscriptionRepository.existsByActionAndUtilisateur(action, ambassadeur)).thenReturn(false);
        when(inscriptionRepository.countByAction(action)).thenReturn(2L);

        assertThatThrownBy(() -> actionService.inscrireAmbassadeur(10L, "amb@test.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Capacité maximale");
    }

    @Test
    void inscrireAmbassadeur_nonAmbassadeur_throwsForbidden() {
        ambassadeur.setRole(Role.ADMIN);
        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));

        assertThatThrownBy(() -> actionService.inscrireAmbassadeur(10L, "amb@test.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ambassadeurs");
    }

    @Test
    void inscrireAmbassadeur_actionInexistante_throwsNotFound() {
        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));
        when(actionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actionService.inscrireAmbassadeur(99L, "amb@test.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Action introuvable");
    }



    @Test
    void desinscrireAmbassadeur_OK() {
        Inscription inscription = new Inscription();
        inscription.setAction(action);
        inscription.setUtilisateur(ambassadeur);

        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));
        when(actionRepository.findById(10L)).thenReturn(Optional.of(action));
        when(inscriptionRepository.findByActionAndUtilisateur(action, ambassadeur)).thenReturn(Optional.of(inscription));

        actionService.desinscrireAmbassadeur(10L, "amb@test.com");

        verify(inscriptionRepository).delete(inscription);
    }

    @Test
    void desinscrireAmbassadeur_inscriptionInexistante_throwsNotFound() {
        when(utilisateurRepository.findByEmail("amb@test.com")).thenReturn(Optional.of(ambassadeur));
        when(actionRepository.findById(10L)).thenReturn(Optional.of(action));
        when(inscriptionRepository.findByActionAndUtilisateur(action, ambassadeur)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actionService.desinscrireAmbassadeur(10L, "amb@test.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Inscription introuvable");
    }



    @Test
    void validerDossier_OK() {
        DocumentResource justificatif = new DocumentResource();
        justificatif.setId(5L);

        Inscription inscription = new Inscription();
        inscription.setIdInscription(1L);
        inscription.setAction(action);
        inscription.setUtilisateur(ambassadeur);
        inscription.setStatutInscription("DOSSIER_EN_COURS_DE_TRAITEMENT");
        inscription.setJustificatifPresence(justificatif);
        inscription.setDateInscription(Instant.now());

        when(inscriptionRepository.findByIdInscriptionAndAction_IdAction(1L, 10L)).thenReturn(Optional.of(inscription));

        Inscription savedInscription = new Inscription();
        savedInscription.setIdInscription(1L);
        savedInscription.setAction(action);
        savedInscription.setUtilisateur(ambassadeur);
        savedInscription.setStatutInscription("VALIDE");
        savedInscription.setJustificatifPresence(justificatif);
        savedInscription.setDateInscription(inscription.getDateInscription());
        when(inscriptionRepository.save(any())).thenReturn(savedInscription);

        InscriptionResponse response = actionService.validerDossier(10L, 1L);

        assertThat(response.getStatutInscription()).isEqualTo("VALIDE");
    }

    @Test
    void validerDossier_dossierDejaTraite_throwsConflict() {
        DocumentResource justificatif = new DocumentResource();
        justificatif.setId(5L);

        Inscription inscription = new Inscription();
        inscription.setStatutInscription("VALIDE");
        inscription.setJustificatifPresence(justificatif);

        when(inscriptionRepository.findByIdInscriptionAndAction_IdAction(1L, 10L)).thenReturn(Optional.of(inscription));

        assertThatThrownBy(() -> actionService.validerDossier(10L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("déjà été traité");
    }

    @Test
    void validerDossier_sansJustificatif_throwsConflict() {
        Inscription inscription = new Inscription();
        inscription.setStatutInscription("DOSSIER_EN_COURS_DE_TRAITEMENT");
        inscription.setJustificatifPresence(null);

        when(inscriptionRepository.findByIdInscriptionAndAction_IdAction(1L, 10L)).thenReturn(Optional.of(inscription));

        assertThatThrownBy(() -> actionService.validerDossier(10L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("justificatif");
    }

    @Test
    void refuserDossier_OK() {
        DocumentResource justificatif = new DocumentResource();
        justificatif.setId(5L);

        Inscription inscription = new Inscription();
        inscription.setIdInscription(1L);
        inscription.setAction(action);
        inscription.setUtilisateur(ambassadeur);
        inscription.setStatutInscription("DOSSIER_EN_COURS_DE_TRAITEMENT");
        inscription.setJustificatifPresence(justificatif);
        inscription.setDateInscription(Instant.now());

        when(inscriptionRepository.findByIdInscriptionAndAction_IdAction(1L, 10L)).thenReturn(Optional.of(inscription));

        Inscription savedInscription = new Inscription();
        savedInscription.setIdInscription(1L);
        savedInscription.setAction(action);
        savedInscription.setUtilisateur(ambassadeur);
        savedInscription.setStatutInscription("REFUSE");
        savedInscription.setJustificatifPresence(justificatif);
        savedInscription.setDateInscription(inscription.getDateInscription());
        when(inscriptionRepository.save(any())).thenReturn(savedInscription);

        InscriptionResponse response = actionService.refuserDossier(10L, 1L);

        assertThat(response.getStatutInscription()).isEqualTo("REFUSE");
    }



    @Test
    void listDossiersEnCours_retourneUniquementDossiersEnCours() {
        DocumentResource justificatif = new DocumentResource();
        justificatif.setId(5L);

        Inscription inscription = new Inscription();
        inscription.setIdInscription(1L);
        inscription.setAction(action);
        inscription.setUtilisateur(ambassadeur);
        inscription.setStatutInscription("DOSSIER_EN_COURS_DE_TRAITEMENT");
        inscription.setJustificatifPresence(justificatif);
        inscription.setDateInscription(Instant.now());

        when(inscriptionRepository
                .findByStatutInscriptionAndJustificatifPresenceIsNotNullOrderByDateInscriptionDesc("DOSSIER_EN_COURS_DE_TRAITEMENT"))
                .thenReturn(List.of(inscription));

        List<InscriptionResponse> result = actionService.listDossiersEnCours();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatutInscription()).isEqualTo("DOSSIER_EN_COURS_DE_TRAITEMENT");
    }
}
