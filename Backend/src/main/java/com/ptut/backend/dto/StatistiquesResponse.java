package com.ptut.backend.dto;

import java.util.List;

public class StatistiquesResponse {

    private Global global;
    private List<ParType> parType;
    private List<ParMois> parMois;

    public StatistiquesResponse(Global global, List<ParType> parType, List<ParMois> parMois) {
        this.global = global;
        this.parType = parType;
        this.parMois = parMois;
    }

    public Global getGlobal() { return global; }
    public List<ParType> getParType() { return parType; }
    public List<ParMois> getParMois() { return parMois; }

    public static class Global {
        private long totalActions;
        private long totalAmbassadeurs;
        private long totalPreuvesValidees;
        private long totalPreuvesEnAttente;

        public Global(long totalActions, long totalAmbassadeurs, long totalPreuvesValidees, long totalPreuvesEnAttente) {
            this.totalActions = totalActions;
            this.totalAmbassadeurs = totalAmbassadeurs;
            this.totalPreuvesValidees = totalPreuvesValidees;
            this.totalPreuvesEnAttente = totalPreuvesEnAttente;
        }

        public long getTotalActions() { return totalActions; }
        public long getTotalAmbassadeurs() { return totalAmbassadeurs; }
        public long getTotalPreuvesValidees() { return totalPreuvesValidees; }
        public long getTotalPreuvesEnAttente() { return totalPreuvesEnAttente; }
    }

    public static class ParType {
        private String type;
        private long total;
        private long validees;
        private long enCours;
        private long aVenir;
        private String couleur;

        public ParType(String type, long total, long validees, long enCours, long aVenir, String couleur) {
            this.type = type;
            this.total = total;
            this.validees = validees;
            this.enCours = enCours;
            this.aVenir = aVenir;
            this.couleur = couleur;
        }

        public String getType() { return type; }
        public long getTotal() { return total; }
        public long getValidees() { return validees; }
        public long getEnCours() { return enCours; }
        public long getAVenir() { return aVenir; }
        public String getCouleur() { return couleur; }
    }

    public static class ParMois {
        private String mois;
        private long actions;

        public ParMois(String mois, long actions) {
            this.mois = mois;
            this.actions = actions;
        }

        public String getMois() { return mois; }
        public long getActions() { return actions; }
    }
}
