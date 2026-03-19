package com.ptut.backend.dto;

import java.time.Instant;

public class InscriptionResponse {

    private Long idInscription;
    private Long idAction;
    private String emailAmbassadeur;
    private Instant dateInscription;
    private String statutInscription;

    public Long getIdInscription() {
        return idInscription;
    }

    public void setIdInscription(Long idInscription) {
        this.idInscription = idInscription;
    }

    public Long getIdAction() {
        return idAction;
    }

    public void setIdAction(Long idAction) {
        this.idAction = idAction;
    }

    public String getEmailAmbassadeur() {
        return emailAmbassadeur;
    }

    public void setEmailAmbassadeur(String emailAmbassadeur) {
        this.emailAmbassadeur = emailAmbassadeur;
    }

    public Instant getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Instant dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getStatutInscription() {
        return statutInscription;
    }

    public void setStatutInscription(String statutInscription) {
        this.statutInscription = statutInscription;
    }
}