package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FireStationRepositoryTest {

    private FireStationRepository repository;

    @BeforeEach
    public void setUp() throws IOException {
        DataRepository dataRepositoryMock = mock(DataRepository.class);

        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("./src/test/resources/dataTest.json");
        JsonNode jsonNode = new ObjectMapper().readTree(jsonFile);

        doReturn(jsonNode).when(dataRepositoryMock).getData();

        repository = new FireStationRepository(dataRepositoryMock);
        JsonNode firestationsNode = jsonNode.get("firestations");

        TypeReference<List<FireStation>> typeReferenceList = new TypeReference<>() {};
        repository.fireStations = objectMapper.readValue(firestationsNode.traverse(), typeReferenceList);
    }

    @Test
    void findAll_shouldReturnAllFireStations() {
        // Act
        List<FireStation> actualFireStations = repository.findAll();

        // Assert
        assertEquals(3, actualFireStations.size());
        assertEquals("Green Gables", actualFireStations.getFirst().getAddress());
        assertEquals("1", actualFireStations.getFirst().getStation());
    }

    @Test
    void getCoveredAddresses_shouldReturnAddressesCoveredByGivenStation() {
        // Act
        ArrayList<String> coveredAddresses = repository.getCoveredAddresses("1");

        // Assert
        assertEquals(2, coveredAddresses.size());
        assertTrue(coveredAddresses.contains("Green Gables"));
    }

    @Test
    void getStationNumber_shouldReturnCorrectNumber() {
        // Act
        String stationNumber = repository.getStationNumber("Green Gables");

        // Assert
        assertEquals("1", stationNumber);
    }

    @Test
    void save_shouldAddFireStationToRepository() {
        // Arrange
        FireStation newFireStation = new FireStation("New Address", "3");

        // Act
        repository.save(newFireStation);

        // Assert
        List<FireStation> fireStations = repository.fireStations;
        assertTrue(fireStations.contains(newFireStation));
        assertEquals(4, fireStations.size());
    }

    @Test
    void delete_shouldRemoveFireStationFromRepository() {
        // Arrange
        FireStation fireStationToDelete = new FireStation("Green Gables", "1");

        // Act
        repository.delete("Green Gables");

        // Assert
        List<FireStation> fireStations = repository.fireStations;
        assertFalse(fireStations.contains(fireStationToDelete));
        assertEquals(2, fireStations.size());
    }

    @Test
    void update_shouldModifyExistingFireStation() {
        // Arrange
        FireStation updatedFireStation = new FireStation("Green Gables", "3");

        // Act
        repository.update(updatedFireStation);

        // Assert
        List<FireStation> fireStations = repository.fireStations;
        assertEquals(3, fireStations.size());
        assertEquals("3", fireStations.getFirst().getStation());
    }


}
