package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.FloodAlertDTO;
import com.safetynet.alerts.controller.dto.PersonsListInCaseOfFireDTO;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
public class FireStationController {

    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);

    private final FireStationService fireStationService;

    /* CONSTRUCTOR */
    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    /* METHODS */

    /**
     * This endpoint is used to fetch a list of all fire stations.
     *
     * @return a list of fire stations in the system.
     *         - 200 OK: successful retrieval of the fire stations.
     *         - 404 NOT FOUND: if no fire stations are found.
     */
    @GetMapping("/firestations")
    public ResponseEntity<List<FireStation>> getFireStations() {
        try {
            List<FireStation> fireStations = fireStationService.getFireStations();
            logger.info("Successful response, found {} fire stations", fireStations.size());
            return ResponseEntity.ok(fireStations);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    /**
     * This endpoint returns detailed information about people covered by a specific fire station number.
     * Example usage:
     * GET /firestation?station_number=1
     *
     * @param station_number a String representing the fire station number for which the persons list is requested.
     * @return a CoveredPersonsListDTO object containing the list of persons whose address is covered by the fire station.
     */
    @GetMapping("/firestation")
    public ResponseEntity<CoveredPersonsListDTO> getFireStationPersonsList(@RequestParam String station_number) {
        logger.debug("Received request to get persons covered by station number: {}", station_number);
        try {
            CoveredPersonsListDTO coveredPersonsList = fireStationService.createFireStationPersonsList(station_number);
            logger.info("Returning covered persons list for station {}, with {} persons covered: {} adults and {} child", station_number, coveredPersonsList.getCoveredPersons().size(), coveredPersonsList.getAdultsCount(), coveredPersonsList.getChildCount());
            return ResponseEntity.ok(coveredPersonsList);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This endpoint returns a list of phone numbers of persons whose address is covered by a specific fire station.
     * Example usage:
     * GET /phoneAlert?fire_station=2
     *
     * @param firestation_number a String representing the station number for which the phone list is requested.
     * @return a set of phone numbers for persons covered by the given fire station.
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<Set<String>> getPhoneList(@RequestParam String firestation_number) {
        logger.debug("Received request for phone numbers for fire station number: {}", firestation_number);
        try {
            Set<String> phoneList = fireStationService.createPhoneList(firestation_number);
            logger.info("Returning {} phone numbers for fire station number: {}", phoneList.size() ,firestation_number);
            return ResponseEntity.ok(phoneList);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptySet());
        }
    }

    /**
     * This endpoint returns a list of persons and their details for a specific address in case of fire.
     * Example usage:
     * GET /fire?address=1509 Culver St
     *
     * @param address the address for which the persons list is requested.
     * @return a PersonsListInCaseOfFireDTO object containing the persons list.
     */
    @GetMapping("/fire")
    public ResponseEntity<PersonsListInCaseOfFireDTO> getPersonsListInCaseOfFire (@RequestParam String address) {
        try {
            PersonsListInCaseOfFireDTO personsList = fireStationService.createPersonsListInCaseOfFire(address);
            logger.info("Returning a list with {} persons for address {}", personsList.getPersonsAtThisAddress().size(), address);
            return ResponseEntity.ok(personsList);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This endpoint returns a list of persons by address in case of flood.
     * Example usage:
     * GET /flood/stations?stations=1,2
     *
     * @param stations a list of String representing the fire stations' numbers involved in the flood.
     * @return a List of FloodAlertDTO objects containing the address and list of persons living there.
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<List<FloodAlertDTO>> getAddressesAndPersonsCovered(@RequestParam List<String> stations) {
        logger.debug("Received request for flood alerts for fire stations: {}", stations);
        try {
            List<FloodAlertDTO> floodAlertList = fireStationService.createFloodAlertList(stations);
            logger.info("Returning the flood alert for stations {} with {} addresses involved", stations, floodAlertList.size());
            return ResponseEntity.ok(floodAlertList);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    /**
     * Adds a new fire station to the system.
     * Accepts a JSON object representing a fire station and adds it to the list of fire stations.
     * Example usage:
     * POST /firestation
     * Body: {"address": "New Address", "station": "3"}
     *
     * @param fireStation a FireStation object in the body of the request representing the station to be added.
     * @return a ResponseEntity with the HTTP Status:
     *         - 201 CREATED: if the fire station was added successfully,
     *         - 204 NO CONTENT: if the fire station could not be added.
     */
    @PostMapping(value = "/firestation")
    public ResponseEntity<FireStation> addFireStation(@RequestBody FireStation fireStation) {
        FireStation fireStationToAdd = fireStationService.createFireStation(fireStation);
        if (Objects.isNull(fireStationToAdd)) {
            logger.error("Failed to add the fire station {}", fireStation);
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{address}")
                .buildAndExpand(fireStationToAdd.getAddress(), fireStationToAdd.getStation())
                .toUri();
        logger.info("Added a new fire station for the address: {}", fireStationToAdd.getAddress());
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates an existing fire station with new values.
     * Example usage:
     * PUT /firestation
     * Body: {"address": "New Address", "station": "1"}
     *
     * @param fireStation a json of the FireStation object with updated details in the body of the request.
     * @return a response entity with the updated fire station:
     *         - 200 OK: If the fire station has been updated.
     *         - 404 NOT FOUND: If the fire station hasn't been updated.
     */
    @PutMapping(value = "/firestation")
    public ResponseEntity<FireStation> updateFireStation(@RequestBody FireStation fireStation) {
        try {
            FireStation fireStationToUpdate = fireStationService.updateFireStation(fireStation);
            logger.info("Successfully updated the fire station {}", fireStation);
            return ResponseEntity.ok(fireStationToUpdate);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Deletes a fire station matching the given address.
     * Example usage:
     * DELETE /firestation?address=New Address
     *
     * @param address a String representing the address of the fire station to be deleted.
     * @return ResponseEntity<Void> indicating the result of the operation:
     *         - 200 OK: if the fire station has been successfully deleted,
     *         - 404 NOT FOUND: if the fire station hasn't been found.
     */
    @DeleteMapping(value = "/firestation")
    public ResponseEntity<Void> deleteFireStation(@RequestParam String address) {
        logger.debug("Received request for delete fire station for the address: {}", address);
        try {
            fireStationService.deleteFireStation(address);
            logger.info("Successfully deleted the fire station for the address: {}", address);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
