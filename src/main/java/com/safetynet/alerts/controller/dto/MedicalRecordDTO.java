package com.safetynet.alerts.controller.dto;

import java.util.ArrayList;

public class MedicalRecordDTO {
    private ArrayList<String> medicine;
    private ArrayList<String> allergies;

    public MedicalRecordDTO(ArrayList<String> medicine, ArrayList<String> allergies) {
        this.medicine = medicine;
        this.allergies = allergies;
    }

    public ArrayList<String> getMedicine() {
        return medicine;
    }

    public ArrayList<String> getAllergies() {
        return allergies;
    }
}
