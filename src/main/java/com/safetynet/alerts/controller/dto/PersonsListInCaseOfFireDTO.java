package com.safetynet.alerts.controller.dto;

import java.util.ArrayList;

public class PersonsListInCaseOfFireDTO {
    String stationNumber;
    ArrayList<PersonAtThisAddressDTO> personsAtThisAddress;

    public PersonsListInCaseOfFireDTO(String stationNumber, ArrayList<PersonAtThisAddressDTO> personsAtThisAddress) {
        this.stationNumber = stationNumber;
        this.personsAtThisAddress = personsAtThisAddress;
    }

    public String getStationNumber() {
        return stationNumber;
    }

    public ArrayList<PersonAtThisAddressDTO> getPersonsAtThisAddress() {
        return personsAtThisAddress;
    }
}