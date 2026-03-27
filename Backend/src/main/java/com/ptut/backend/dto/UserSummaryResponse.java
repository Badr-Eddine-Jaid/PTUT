package com.ptut.backend.dto;

import java.util.List;

public class UserSummaryResponse {

    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private List<ActionBriefResponse> actions;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(Long idUtilisateur, String nom, String prenom, String email, String role) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<ActionBriefResponse> getActions() {
        return actions;
    }

    public void setActions(List<ActionBriefResponse> actions) {
        this.actions = actions;
    }
}