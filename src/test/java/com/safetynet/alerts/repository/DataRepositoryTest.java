package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.FireStation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataRepositoryTest {

    private DataRepository dataRepository;

    @BeforeEach
    void setUp() {
        dataRepository = new DataRepository("./src/test/resources/dataTest.json");
    }

    @AfterEach
    public void restoreOriginalFile() throws IOException {
        Files.copy(Paths.get("./src/test/resources/originalDataTest.json"),
                Paths.get("./src/test/resources/dataTest.json"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void getData_shouldReturnJsonNode_whenFileIsValid() {
        // Act
        JsonNode result = dataRepository.getData();

        // Assert
        assertNotNull(result);

        JsonNode personsNode = result.get("persons");
        assertNotNull(personsNode);
        JsonNode fireStationsNode = result.get("firestations");
        assertNotNull(fireStationsNode);
        JsonNode medicalRecordsNode = result.get("medicalrecords");
        assertNotNull(medicalRecordsNode);

        assertEquals("Anne", personsNode.get(0).get("firstName").asText());
        assertEquals("Green Gables", fireStationsNode.get(0).get("address").asText());
        assertEquals("Josephine", medicalRecordsNode.get(4).get("firstName").asText());
    }

    @Test
    void writeData_shouldWriteCorrectDataToFile() {
        // Arrange
        List<FireStation> newFireStations = new ArrayList<>();
        newFireStations.add(new FireStation("Cherry Valley", "1"));
        newFireStations.add(new FireStation("Great Station", "2"));
        newFireStations.add(new FireStation("Long lane", "3"));

        ObjectNode originalData = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode newDatas = originalData.set("firestations", objectMapper.valueToTree(newFireStations));

        // Act
        dataRepository.writeData(newDatas);

        // Assert
        JsonNode updatedData = dataRepository.getData();
        JsonNode updatedFireStations = updatedData.get("firestations");
        assertNotNull(updatedFireStations);
        assertEquals(3, updatedFireStations.size());
        assertEquals("Cherry Valley", updatedFireStations.get(0).get("address").asText());
        assertEquals("2", updatedFireStations.get(1).get("station").asText());
    }

}