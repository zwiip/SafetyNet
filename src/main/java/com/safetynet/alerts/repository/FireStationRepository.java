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
    List<FireStation> fireStations;
    private final DataRepository dataRepository;

    public FireStationRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListFireStations();
    }

    public void createListFireStations() {
        try {
            JsonNode data = dataRepository.getData();
            JsonNode fireStatioNode = data.get("firestations");
            TypeReference<List<FireStation>> typeReferenceList = new TypeReference<List<FireStation>>() {};
            List<FireStation> fireStations = new ObjectMapper().readValue(fireStatioNode.traverse(), typeReferenceList);
            this.fireStations = fireStations;
        } catch (IOException e) {
            throw new RuntimeException("Error while creating FireStations List", e);
        }
    }

    public List<FireStation> findAll() {
        return fireStations;
    }

    public ArrayList<String> getCoveredAddresses(String stationNumber) {
        ArrayList<String> coveredAddresses = new ArrayList<>();

        for(FireStation firesStation : findAll()) {
            if(firesStation.getStation().equals(stationNumber)) {
                coveredAddresses.add(firesStation.getAddress());
            }
        }
        return coveredAddresses;
    }

    public String getStationNumber(String address) {
        for(FireStation firesStation : findAll()) {
            if(firesStation.getAddress().equals(address)) {
                return firesStation.getStation();
            }
        }
        return null;
    }

    public FireStation save(FireStation fireStation) throws IOException {
        fireStations.add(fireStation);
        updateFireStationsList(fireStations);
        return fireStation;
    }

    public void delete(FireStation inputFireStation) throws IOException {
        for (FireStation firesStation : fireStations) {
            if(firesStation.getAddress().equals(inputFireStation.getAddress()) &&
               firesStation.getStation().equals(inputFireStation.getStation())) {
                fireStations.remove(firesStation);
                updateFireStationsList(fireStations);
                return;
            }
        }
        throw new IllegalArgumentException("FireStation not found: " + inputFireStation);
    }

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

    public void updateFireStationsList(List<FireStation> fireStations) {
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        rootNode.set("firestations", objectMapper.valueToTree(fireStations));
        dataRepository.writeData(rootNode);
    }
}
