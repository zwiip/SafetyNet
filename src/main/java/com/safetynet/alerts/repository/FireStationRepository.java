package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.FireStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<FireStation>> typeReferenceList = new TypeReference<>() {};
            List<FireStation> fireStationsData = objectMapper.readValue(fireStationsNode.traverse(), typeReferenceList);

            this.fireStations = validateFireStationsData(fireStationsData);
            updateFireStationsList(this.fireStations);
            logger.info("Successfully created fire stations list with {} fire stations.", fireStations.size());
        } catch (IOException e) {
            throw new RuntimeException("Error while creating FireStations List", e);
        }
    }

    /**
     * Validates the list of FireStations by removing any duplicate entries.
     * A duplicate is identified when two firestations map have the same address.
     * If duplicates are found, they are removed.
     *
     * @param fireStations the list of Firestation objects to validate.
     * @return a new list of Firestation objects with duplicates removed.
     */
    public List<FireStation> validateFireStationsData(List<FireStation> fireStations) {
        Set<String> uniqueAddresses = new HashSet<>();
        List<FireStation> filteredFireStations = new ArrayList<>();

        for (FireStation fireStation : fireStations) {
            if (!uniqueAddresses.contains(fireStation.getAddress())) {
                uniqueAddresses.add(fireStation.getAddress());
                filteredFireStations.add(fireStation);
            } else {
                logger.warn("Duplicate fire station found and removed for address {}", fireStation.getAddress());
            }
        }
        logger.debug("Validation complete. Total fire stations after removing duplicates: {}", filteredFireStations.size());
        return filteredFireStations;
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

    public FireStation getFireStationByAddress(String inputAddress) {
        logger.debug("Looking for fire station for the address: {}", inputAddress);
        for (FireStation firesStation : fireStations) {
            if (firesStation.getAddress().equals(inputAddress)) {
                logger.debug("Found the fire station: {}", firesStation);
                return firesStation;
            }
        }
        logger.warn("FireStation not found for the address: {}", inputAddress);
        return null;
    }

    /**
     * Browse through the fire stations to create a list of addresses covered by the given station number.
     *
     * @param stationNumber a String representing the number of the fire station.
     * @return a List of String with the addresses covered by the fire station.
     */
    public ArrayList<String> getCoveredAddresses(String stationNumber) {
        logger.debug("Fetching addresses covered by station number: {}", stationNumber);
        ArrayList<String> coveredAddresses = new ArrayList<>();
        for(FireStation firesStation : findAll()) {
            if(firesStation.getStation().equals(stationNumber)) {
                coveredAddresses.add(firesStation.getAddress());
                logger.debug("Adding to the covered addresses list: {}", firesStation.getAddress());
            }
        }
        logger.debug("Found {} addresses covered by station number: {}", coveredAddresses.size(), stationNumber);
        return coveredAddresses;
    }

    /**
     * Browse through the fire stations looking for the number of the station covering the given address.
     *
     * @param address a String of an address.
     * @return a String representing the number of the fire station covering this address.
     */
    public String getStationNumber(String address) {
        logger.debug("Fetching station number for address: {}", address);
        FireStation fireStation = getFireStationByAddress(address);
        if (fireStation != null) {
            logger.info("Station number found for address: {}", address);
            return fireStation.getStation();
        }
        return null;
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
     * Update an existing fire station with the new data and update the JSON file
     *
     * @param inputFireStation a Fire Station with updated data
     * @return the updated FireStation
     */
    public FireStation update(FireStation inputFireStation) {
        logger.debug("Updating fire station: {}", inputFireStation);
        FireStation fireStationToUpdate = getFireStationByAddress(inputFireStation.getAddress());
        fireStations.set(fireStations.indexOf(fireStationToUpdate), inputFireStation);
        updateFireStationsList(fireStations);
        logger.info("Fire station updated successfully: {}", inputFireStation);
        return inputFireStation;
    }

    /**
     * Delete the fireStation matching the given address and update the JSON file
     *
     * @param inputAddress a String representing the address we want to delete
     */
    public void delete(String inputAddress) {
        logger.debug("Deleting fire station with address: {}", inputAddress);
        FireStation fireStationToDelete = getFireStationByAddress(inputAddress);
        fireStations.remove(fireStationToDelete);
        updateFireStationsList(fireStations);
        logger.info("Fire station with address {} deleted successfully.", inputAddress);
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
        logger.info("Fire stations list updated successfully, now {} fire stations.", fireStations.size());
    }
}
