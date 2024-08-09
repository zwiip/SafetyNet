package com.safetynet.alerts.controller.dto;

import java.util.ArrayList;

public class PersonsListInCaseOfFireDTO {
    String stationNumber;
    ArrayList<PersonsAtThisAddressDTO> personsAtThisAddress;

    public PersonsListInCaseOfFireDTO(String stationNumber, ArrayList<PersonsAtThisAddressDTO> personsAtThisAddress) {
        this.stationNumber = stationNumber;
        this.personsAtThisAddress = personsAtThisAddress;
    }

    public String getStationNumber() {
        return stationNumber;
    }

    public ArrayList<PersonsAtThisAddressDTO> getPersonsAtThisAddress() {
        return personsAtThisAddress;
    }
}