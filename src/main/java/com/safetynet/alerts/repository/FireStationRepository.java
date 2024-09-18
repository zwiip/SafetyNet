package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FireStationRepository {
    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(FireStationRepository.class);
    List<FireStation> fireStations;
    private final DataRepository dataRepository;

    /* CONSTRUCTOR */
    public FireStationRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListFireStations();
    }

    /* METHODS */

    /**
     * Take a JsonNode and fetch the values for the key "firestations" in order to create a list of FireStations
     *
     * @throws RuntimeException if an error occurs while creating the list
     */
    public void createListFireStations() {
        try {
            logger.debug("Creating fire stations list from JSON file.");
            JsonNode data = dataRepository.getData();
            JsonNode fireStationsNode = data.get("firestations");
            TypeReference<List<FireStation>> typeReferenceList = new TypeReference<>() {};
            this.fireStations = new ObjectMapper().readValue(fireStationsNode.traverse(), typeReferenceList);
            logger.info("Successfully created fire stations list with {} fire stations.", fireStations.size());
        } catch (IOException e) {
            throw new RuntimeException("Error while creating FireStations List", e);
        }
    }

    /**
     * Retrieves the list of all fire stations
     *
     * @return the list of fire stations
     */
    public List<FireStation> findAll() {
        logger.debug("Fetching all fire stations.");
        return fireStations;
    }

    /**
     * Browse through the fire stations to create a list of addresses covered by the given station number.
     *
     * @param stationNumber a String representing the number of the fire station.
     * @return a List of String with the addresses covered by the fire station.
     * @throws ResourceNotFoundException if there is no addresses matching the station number.
     */
    public ArrayList<String> getCoveredAddresses(String stationNumber) {
        logger.debug("Fetching addresses covered by station number: {}", stationNumber);
        ArrayList<String> coveredAddresses = new ArrayList<>();
        for(FireStation firesStation : findAll()) {
            if(firesStation.getStation().equals(stationNumber)) {
                coveredAddresses.add(firesStation.getAddress());
            }
        }
        logger.debug("Found {} addresses covered by station number: {}", coveredAddresses.size(), stationNumber);
        if(coveredAddresses.isEmpty()) {
            throw new ResourceNotFoundException("No addresses found for given station number: " + stationNumber);
        }
        return coveredAddresses;
    }

    /**
     * Browse through the fire stations looking for the number of the station covering the given address.
     *
     * @param address a String of an address.
     * @return a String representing the number of the fire station covering this address.
     * @throws ResourceNotFoundException if the address doesn't match any station number.
     */
    public String getStationNumber(String address) {
        logger.debug("Fetching station number for address: {}", address);
        for(FireStation firesStation : findAll()) {
            if(firesStation.getAddress().equals(address)) {
                logger.info("Station number found for address: {}", address);
                return firesStation.getStation();
            }
        }
        throw new ResourceNotFoundException("No station number found matching the address: " + address);
    }

    /**
     * Add a new fire station to the list and update the JSON file.
     *
     * @param fireStation a new Fire Station to add.
     * @return the added fire station.
     */
    public FireStation save(FireStation fireStation) {
        logger.debug("Saving new fire station: {}", fireStation);
        fireStations.add(fireStation);
        updateFireStationsList(fireStations);
        logger.info("Fire station saved successfully.");
        return fireStation;
    }

    /**
     * Delete the fireStation matching the given address and update the JSON file
     *
     * @param inputAddress a String representing the address we want to delete
     * @throws IllegalArgumentException if no fire station is found with the given address
     */
    public boolean delete(String inputAddress) {
        logger.debug("Deleting fire station with address: {}", inputAddress);
        for (FireStation firesStation : fireStations) {
            if(firesStation.getAddress().equals(inputAddress)) {
                fireStations.remove(firesStation);
                updateFireStationsList(fireStations);
                logger.info("Fire station with address {} deleted successfully.", inputAddress);
                return true;
            }
        }
        throw new ResourceNotFoundException("FireStation not found: " + inputAddress);
    }

    /**
     * Update an existing fire station with the new data and update the JSON file
     *
     * @param inputFireStation a Fire Station with updated data
     * @return the updated FireStation
     * @throws IllegalArgumentException if no fire station is found with the given address
     */
    public FireStation update(FireStation inputFireStation) {
        logger.debug("Updating fire station: {}", inputFireStation);
        for (FireStation firesStation : fireStations) {
            if(firesStation.getAddress().equals(inputFireStation.getAddress())) {
                fireStations.set(fireStations.indexOf(firesStation), inputFireStation);
                updateFireStationsList(fireStations);
                logger.info("Fire station updated successfully: {}", inputFireStation);
                return inputFireStation;
            }
        }
        throw new ResourceNotFoundException("FireStation not found: " + inputFireStation);
    }

    /**
     * Turn the Fire Station list into a JsonNode object in order to write in the JSON file as values for the key "firestations"
     *
     * @param fireStations the list of fire stations with new data to write to the JSON File
     */
    public void updateFireStationsList(List<FireStation> fireStations) {
        logger.debug("Updating fire stations list in the JSON file.");
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        rootNode.set("firestations", objectMapper.valueToTree(fireStations));
        dataRepository.writeData(rootNode);
        logger.info("Fire stations list updated successfully.");
    }
}
