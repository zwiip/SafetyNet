package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.FloodAlertDTO;
import com.safetynet.alerts.controller.dto.PersonsListInCaseOfFireDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
public class FireStationController {

    private final FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

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
        return fireStationService.createPersonsListInCaseOfFire(address);
    }

    @GetMapping("/flood/stations")
    public List<FloodAlertDTO> getAddressesAndPersonsCovered(@RequestParam List<String> stations) {
        return fireStationService.createFloodAlertList(stations);
    }

    @PostMapping(value = "/firestation")
    public ResponseEntity<FireStation> addFireStation(@RequestBody FireStation firestation) throws IOException {
        FireStation fireStationToAdd = fireStationService.createFireStation(firestation);
        if (Objects.isNull(fireStationToAdd)) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{address}")
                .buildAndExpand(fireStationToAdd.getAddress(), fireStationToAdd.getStation())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/firestation")
    public void deleteFireStation(@RequestParam String address) throws IOException {
        fireStationService.deleteFireStation(address);
    }

    @PutMapping(value = "/firestation")
    public ResponseEntity<FireStation> updateFireStation(@RequestBody FireStation firestation) {
        FireStation fireStationToUpdate = fireStationService.updateFireStation(firestation);
        if (Objects.isNull(fireStationToUpdate)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fireStationToUpdate);
    }
}
