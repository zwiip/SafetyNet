package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class FireStationRepository {
    List<FireStation> fireStations;

    public void createListFireStations(JsonNode jsonNode) throws IOException {
        JsonNode fireStatioNode = jsonNode.get("firestations");
        TypeReference<List<FireStation>> typeReferenceList = new TypeReference<List<FireStation>>() {};
        List<FireStation> fireStations = new ObjectMapper().readValue(fireStatioNode.traverse(), typeReferenceList);
        this.fireStations = fireStations;
    }

    public List<FireStation> findAll() {
        return fireStations;
    }
}
