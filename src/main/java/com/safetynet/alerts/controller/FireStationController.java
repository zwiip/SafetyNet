package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FireStationController {

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestations")
    public List<FireStation> getFireStations() {
        return fireStationService.getFireStations();
    }
}
