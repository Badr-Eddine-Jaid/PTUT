package com.ptut.backend.service;

import com.ptut.backend.dto.StatistiquesResponse;
import com.ptut.backend.entity.Inscription;
import com.ptut.backend.entity.Role;
import com.ptut.backend.repository.ActionRepository;
import com.ptut.backend.repository.InscriptionRepository;
import com.ptut.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistiquesService {

    // Couleurs corrigées selon le design system
    private static final Map<String, String> COULEURS = new LinkedHashMap<>();
    static {
        COULEURS.put("SALON ÉTUDIANT",  "#004741");
        COULEURS.put("LYCÉE",           "#856404");
        COULEURS.put("RÉSEAUX SOCIAUX", "#C4507A");
        COULEURS.put("FORMATION",       "#E8734A");
    }

    private static final String[] MOIS_LABELS = {
            "Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
            "Juil", "Août", "Sep", "Oct", "Nov", "Déc"
    };

    private final ActionRepository actionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final InscriptionRepository inscriptionRepository;

    public StatistiquesService(ActionRepository actionRepository,
                               UtilisateurRepository utilisateurRepository,
                               InscriptionRepository inscriptionRepository) {
        this.actionRepository = actionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.inscriptionRepository = inscriptionRepository;
    }

    @Transactional(readOnly = true)
    public StatistiquesResponse calculer() {
        List<Inscription> inscriptions = inscriptionRepository.findAll();

        // Global : totalActions = nombre d'inscriptions (pas nombre d'actions dans le catalogue)
        long totalActions = inscriptions.size();
        long totalAmbassadeurs = utilisateurRepository.findAllByRole(Role.AMBASSADEUR).size();
        long totalPreuvesValidees = inscriptions.stream()
                .filter(i -> "VALIDE".equals(i.getStatutInscription())).count();
        long totalPreuvesEnAttente = inscriptions.stream()
                .filter(i -> "DOSSIER_EN_COURS_DE_TRAITEMENT".equals(i.getStatutInscription())).count();

        StatistiquesResponse.Global global = new StatistiquesResponse.Global(
                totalActions, totalAmbassadeurs, totalPreuvesValidees, totalPreuvesEnAttente
        );

        // Par type : enCours = INSCRIT + DOSSIER_EN_COURS_DE_TRAITEMENT
        Map<String, long[]> parTypeMap = new LinkedHashMap<>();
        for (String type : COULEURS.keySet()) {
            parTypeMap.put(type, new long[]{0, 0, 0, 0}); // total, validees, enCours, aVenir
        }

        for (Inscription i : inscriptions) {
            String type = i.getAction().getTypeAction();
            if (type == null || !parTypeMap.containsKey(type)) continue;
            long[] stats = parTypeMap.get(type);
            stats[0]++;
            switch (i.getStatutInscription()) {
                case "VALIDE"                          -> stats[1]++;
                case "INSCRIT", "DOSSIER_EN_COURS_DE_TRAITEMENT" -> stats[2]++;
            }
        }

        List<StatistiquesResponse.ParType> parType = new ArrayList<>();
        parTypeMap.forEach((type, stats) -> parType.add(
                new StatistiquesResponse.ParType(type, stats[0], stats[1], stats[2], 0, COULEURS.get(type))
        ));

        // Par mois : grouper par date de l'action (date_action), pas date d'inscription
        long[] parMoisCount = new long[12];
        for (Inscription i : inscriptions) {
            if (i.getAction().getDateAction() == null) continue;
            int mois = i.getAction().getDateAction().getMonthValue() - 1;
            parMoisCount[mois]++;
        }

        List<StatistiquesResponse.ParMois> parMois = new ArrayList<>();
        for (int m = 0; m < 12; m++) {
            if (parMoisCount[m] > 0) {
                parMois.add(new StatistiquesResponse.ParMois(MOIS_LABELS[m], parMoisCount[m]));
            }
        }

        return new StatistiquesResponse(global, parType, parMois);
    }
}
