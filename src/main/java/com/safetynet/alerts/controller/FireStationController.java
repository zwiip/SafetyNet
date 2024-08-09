package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.PersonDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("phoneAlert")
    public ArrayList<String> getPhoneList(@RequestParam String firestation){
        return fireStationService.createPhoneList(firestation);
    }
}
