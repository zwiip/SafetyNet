package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.FloodAlertDTO;
import com.safetynet.alerts.controller.dto.PersonsListInCaseOfFireDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class FireStationController {

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestations")
    public List<FireStation> getFireStations() {
        return fireStationService.getFireStations();
    }

    @GetMapping("/firestation")
    public CoveredPersonsListDTO getFireStationPersonsList(@RequestParam String stationNumber) {
        return fireStationService.createFireStationPersonsList(stationNumber);
    }

    @GetMapping("/phoneAlert")
    public Set<String> getPhoneList(@RequestParam String firestation){
        return fireStationService.createPhoneList(firestation);
    }

    @GetMapping("/fire")
    public PersonsListInCaseOfFireDTO getPersonsAtThisAddress(@RequestParam String address) {
        return fireStationService.createPersonsAtThisAddressList(address);
    }

    @GetMapping("/flood/stations")
    public List<FloodAlertDTO> getAddressesAndPersonsCovered(@RequestParam List<String> stations) {
        return fireStationService.createFloodAlertList(stations);
    }

}
