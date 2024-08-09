package com.safetynet.alerts.controller.dto;

import java.util.ArrayList;

public class CoveredPersonsListDTO {
    private final int childCount;
    private final int adultsCount;

    ArrayList<PersonDTO> coveredPersons;

    public CoveredPersonsListDTO(int childCount, int adultsCount, ArrayList<PersonDTO> coveredPersons) {
        this.childCount = childCount;
        this.adultsCount = adultsCount;
        this.coveredPersons = coveredPersons;
    }

    public int getChildCount() {
        return childCount;
    }

    public int getAdultsCount() {
        return adultsCount;
    }

    public ArrayList<PersonDTO> getCoveredPersons() {
        return coveredPersons;
    }
}
