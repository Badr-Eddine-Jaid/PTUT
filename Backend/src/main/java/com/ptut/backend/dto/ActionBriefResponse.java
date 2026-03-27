package com.ptut.backend.dto;

import java.time.LocalDate;

public class ActionBriefResponse {

    private Long idAction;
    private String titre;
    private String typeAction;
    private LocalDate dateAction;
    private String statutInscription;

    public ActionBriefResponse() {
    }

    public ActionBriefResponse(Long idAction, String titre, String typeAction, LocalDate dateAction, String statutInscription) {
        this.idAction = idAction;
        this.titre = titre;
        this.typeAction = typeAction;
        this.dateAction = dateAction;
        this.statutInscription = statutInscription;
    }

    public Long getIdAction() {
        return idAction;
    }

    public void setIdAction(Long idAction) {
        this.idAction = idAction;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(String typeAction) {
        this.typeAction = typeAction;
    }

    public LocalDate getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDate dateAction) {
        this.dateAction = dateAction;
    }

    public String getStatutInscription() {
        return statutInscription;
    }

    public void setStatutInscription(String statutInscription) {
        this.statutInscription = statutInscription;
    }
}
