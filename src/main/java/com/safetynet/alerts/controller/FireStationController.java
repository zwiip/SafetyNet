package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.FloodAlertDTO;
import com.safetynet.alerts.controller.dto.PersonsListInCaseOfFireDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public List<FireStation> getFireStations() {
        List<FireStation> fireStations = fireStationService.getFireStations();
        if(fireStations.isEmpty()) {
            logger.warn("No fire station found");
        } else {
            logger.info("Successful response, found {} fire stations", fireStations.size());
        }
        return fireStations;
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
    public CoveredPersonsListDTO getFireStationPersonsList(@RequestParam String station_number) {
        logger.debug("Received request to get persons covered by station number: {}", station_number);
        CoveredPersonsListDTO coveredPersonsList = fireStationService.createFireStationPersonsList(station_number);
        if(coveredPersonsList == null) {
            logger.warn("No person covered by station number: {}", station_number);
        } else {
            logger.info("Returning covered persons list for station {}, with {} persons covered: {} adults and {} child", station_number, coveredPersonsList.getCoveredPersons().size(), coveredPersonsList.getAdultsCount(), coveredPersonsList.getChildCount());
        }
        return coveredPersonsList;
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
    public Set<String> getPhoneList(@RequestParam String firestation_number) {
        logger.debug("Received request for phone numbers for fire station number: {}", firestation_number);
        Set<String> phoneList = fireStationService.createPhoneList(firestation_number);
        if(phoneList == null) {
            logger.warn("No phone number found for fire station number: {}", firestation_number);
        } else {
            logger.info("Returning {} phone numbers for fire station number: {}", phoneList.size() ,firestation_number);
        }
        return phoneList;
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
    public PersonsListInCaseOfFireDTO getPersonsListInCaseOfFire (@RequestParam String address) {

        PersonsListInCaseOfFireDTO personsList = fireStationService.createPersonsListInCaseOfFire(address);
        if(personsList == null) {
            logger.warn("No persons found for fire address: {}", address);
        } else {
            logger.info("Returning a list with {} persons for address {}", personsList.getPersonsAtThisAddress().size(), address);
        }
        return personsList;
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
    public List<FloodAlertDTO> getAddressesAndPersonsCovered(@RequestParam List<String> stations) {
        logger.debug("Received request for flood alerts for fire stations: {}", stations);
        List<FloodAlertDTO> floodAlertList = fireStationService.createFloodAlertList(stations);
        if(floodAlertList == null) {
            logger.warn("Could not compose the flood alert for stations: {}", stations);
        } else {
            logger.info("Returning the flood alert for stations {} with {} addresses involved", stations, floodAlertList.size());
        }
        return floodAlertList;
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
            logger.warn("Failed to add the fire station {}", fireStation);
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
        FireStation fireStationToUpdate = fireStationService.updateFireStation(fireStation);
        if (Objects.isNull(fireStationToUpdate)) {
            logger.warn("Failed to update the fire station {}", fireStation);
            return ResponseEntity.notFound().build();
        }
        logger.info("Successfully updated the fire station {}", fireStation);
        return ResponseEntity.ok(fireStationToUpdate);
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
        if (!fireStationService.deleteFireStation(address)) {
            logger.warn("Failed to delete the fire station for the address: {}", address);
            return ResponseEntity.notFound().build();
        }
        logger.info("Successfully deleted the fire station for the address: {}", address);
        return ResponseEntity.ok().build();
    }
}
