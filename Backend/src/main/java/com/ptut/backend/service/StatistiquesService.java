package com.ptut.backend.service;

import com.ptut.backend.dto.StatistiquesResponse;
import com.ptut.backend.entity.Inscription;
import com.ptut.backend.entity.Role;
import com.ptut.backend.repository.ActionRepository;
import com.ptut.backend.repository.InscriptionRepository;
import com.ptut.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistiquesService {

    private static final Map<String, String> COULEURS = Map.of(
            "SALON ÉTUDIANT",  "#0D47A1",
            "LYCÉE",           "#856404",
            "RÉSEAUX SOCIAUX", "#7B1FA2",
            "FORMATION",       "#3CBEBE"
    );

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

        // Global
        long totalActions = actionRepository.count();
        long totalAmbassadeurs = utilisateurRepository.findAllByRole(Role.AMBASSADEUR).size();
        long totalPreuvesValidees = inscriptions.stream()
                .filter(i -> "VALIDE".equals(i.getStatutInscription())).count();
        long totalPreuvesEnAttente = inscriptions.stream()
                .filter(i -> "DOSSIER_EN_COURS_DE_TRAITEMENT".equals(i.getStatutInscription())).count();

        StatistiquesResponse.Global global = new StatistiquesResponse.Global(
                totalActions, totalAmbassadeurs, totalPreuvesValidees, totalPreuvesEnAttente
        );

        // Par type
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
                case "DOSSIER_EN_COURS_DE_TRAITEMENT"  -> stats[2]++;
                case "INSCRIT"                         -> stats[3]++;
            }
        }

        List<StatistiquesResponse.ParType> parType = new ArrayList<>();
        parTypeMap.forEach((type, stats) -> parType.add(
                new StatistiquesResponse.ParType(type, stats[0], stats[1], stats[2], stats[3], COULEURS.get(type))
        ));

        // Par mois
        long[] parMoisCount = new long[12];
        for (Inscription i : inscriptions) {
            if (i.getDateInscription() == null) continue;
            int mois = i.getDateInscription().atZone(ZoneId.systemDefault()).getMonthValue() - 1;
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
