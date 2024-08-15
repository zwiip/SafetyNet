package com.safetynet.alerts.controller.dto;

import java.util.List;

public class FloodAlertDTO {
    String address;
    List<PersonAtThisAddressDTO> personsAtThisAddressList;

    public FloodAlertDTO(String address,List<PersonAtThisAddressDTO> personsAtThisAddressList) {
        this.address = address;
        this.personsAtThisAddressList = personsAtThisAddressList;
    }

    public String getAddress() {
        return address;
    }

    public List<PersonAtThisAddressDTO> getPersonsAtThisAddressList() {
        return personsAtThisAddressList;
    }
}
