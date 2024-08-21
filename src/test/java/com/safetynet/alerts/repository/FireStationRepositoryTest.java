package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FireStationRepositoryTest {
    private FireStationRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new FireStationRepository();
    }

    @Test
    void createListFireStations_shouldPopulateFireStationsList() throws IOException {
        // Arrange
        String jsonContent = "{ \"firestations\": [" +
                "{\"address\": \"1509 Culver St\", \"station\": \"3\"}," +
                "{\"address\": \"29 15th St\", \"station\": \"2\"}" +
                "] }";
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        // Act
        repository.createListFireStations(jsonNode);

        // Assert
        List<FireStation> fireStations = repository.findAll();
        assertEquals(2, fireStations.size());
        assertEquals("1509 Culver St", fireStations.get(0).getAddress());
        assertEquals("2", fireStations.get(1).getStation());
    }

    @Test
    void findAll_shouldReturnAllFireStations() {
        List<FireStation> expectedFireStations = new ArrayList<>();
        expectedFireStations.add(new FireStation("1509 Culver St", "3"));
        expectedFireStations.add(new FireStation("29 15th St", "2"));

        repository.fireStations = expectedFireStations;

        // Act
        List<FireStation> actualFireStations = repository.findAll();

        // Assert
        assertEquals(expectedFireStations, actualFireStations);

    }
}
