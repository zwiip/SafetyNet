package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.FireStation;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FireStationRepository {
    /** VARIABLES **/
    List<FireStation> fireStations;
    private final DataRepository dataRepository;

    /** CONSTRUCTOR **/
    public FireStationRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListFireStations();
    }

    /**
     * Take a JsonNode and fetch the values for the key "fireStations" in order to create a list of FireStations
     *
     * @throws RuntimeException if an error occur while creating the list
     */
    public void createListFireStations() {
        try {
            JsonNode data = dataRepository.getData();
            JsonNode fireStationsNode = data.get("firestations");
            TypeReference<List<FireStation>> typeReferenceList = new TypeReference<>() {};
            this.fireStations = new ObjectMapper().readValue(fireStationsNode.traverse(), typeReferenceList);
        } catch (IOException e) {
            throw new RuntimeException("Error while creating FireStations List", e);
        }
    }

    /**
     * @return the list of fireStations
     */
    public List<FireStation> findAll() {
        return fireStations;
    }

    /**
     * Browse through the fireStations to create a list of addresses covered by the given station number
     *
     * @param stationNumber a String representing the number of the station
     * @return a List of String with the addresses covered by the station number
     */
    public ArrayList<String> getCoveredAddresses(String stationNumber) {
        ArrayList<String> coveredAddresses = new ArrayList<>();

        for(FireStation firesStation : findAll()) {
            if(firesStation.getStation().equals(stationNumber)) {
                coveredAddresses.add(firesStation.getAddress());
            }
        }
        return coveredAddresses;
    }

    /**
     * Browse through the fireStations looking for the number of the station covering the given address
     *
     * @param address a String of an address
     * @return a String representing the number of the fireStation covering this address
     */
    public String getStationNumber(String address) {
        for(FireStation firesStation : findAll()) {
            if(firesStation.getAddress().equals(address)) {
                return firesStation.getStation();
            }
        }
        return null;
    }

    /**
     * Add a new fireStation to the list and update the file.
     *
     * @param fireStation a new FireStation to add
     * @return the added fireStation
     */
    public FireStation save(FireStation fireStation) {
        fireStations.add(fireStation);
        updateFireStationsList(fireStations);
        return fireStation;
    }

    /**
     * Delete the fireStation matching the given address
     *
     * @param inputAddress a String representing the address we want to delete
     */
    public void delete(String inputAddress) {
        for (FireStation firesStation : fireStations) {
            if(firesStation.getAddress().equals(inputAddress)) {
                fireStations.remove(firesStation);
                updateFireStationsList(fireStations);
                return;
            }
        }
        throw new IllegalArgumentException("FireStation not found: " + inputAddress);
    }

    /**
     * Update a given fireStation with the new data
     *
     * @param inputFireStation a FireStation with updated data
     * @return the updated FireStation
     */
    public FireStation update(FireStation inputFireStation) {
        for (FireStation firesStation : fireStations) {
            if(firesStation.getAddress().equals(inputFireStation.getAddress())) {
                fireStations.set(fireStations.indexOf(firesStation), inputFireStation);
                updateFireStationsList(fireStations);
                return inputFireStation;
            }
        }
        throw new IllegalArgumentException("FireStation not found: " + inputFireStation);
    }

    /**
     * Turn the FireStation list into a JsonNode in order to write in the JSON file as values for the key "firestations"
     *
     * @param fireStations the list of fireStations with new datas to write to the JSON File
     */
    public void updateFireStationsList(List<FireStation> fireStations) {
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        rootNode.set("firestations", objectMapper.valueToTree(fireStations));
        dataRepository.writeData(rootNode);
    }
}
