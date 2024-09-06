package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FireStationRepositoryTest {

    @Autowired
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

        TypeReference<List<FireStation>> typeReferenceList = new TypeReference<List<FireStation>>() {};
        repository.fireStations = objectMapper.readValue(firestationsNode.traverse(), typeReferenceList);
    }

    @AfterEach
    public void restoreOriginalFile() throws IOException {
        Files.copy(Paths.get("./src/test/resources/originalDataTest.json"),
                Paths.get("./src/test/resources/dataTest.json"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void findAll_shouldReturnAllFireStations() {
        // Act
        List<FireStation> actualFireStations = repository.findAll();

        // Assert
        assertEquals(3, actualFireStations.size());
        assertEquals("Green Gables", actualFireStations.get(0).getAddress());
        assertEquals("1", actualFireStations.get(0).getStation());
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
    void getCoveredAddresses_shouldReturnEmptyList_whenNoAddressesFoundForStation() {
        // Act
        ArrayList<String> coveredAddresses = repository.getCoveredAddresses("Unknown address");

        // Assert
        assertTrue(coveredAddresses.isEmpty());
    }

    @Test
    void getStationNumber_shouldReturnCorrectNumber() {
        // Act
        String stationNumber = repository.getStationNumber("Green Gables");

        // Assert
        assertEquals("1", stationNumber);
    }

    @Test
    void getStationNumber_shouldReturnNull_whenNoAddressFound() {
        // Act
        String stationNumber = repository.getStationNumber("Unknown address");

        // Assert
        assertNull(stationNumber);
    }

    @Test
    void save_shouldAddFireStationToRepository() throws IOException {
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
    void delete_shouldRemoveFireStationFromRepository() throws IOException {
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
    void update_shouldModifyExistingFireStation() throws IOException {
        // Arrange
        FireStation updatedFireStation = new FireStation("Green Gables", "3");

        // Act
        repository.update(updatedFireStation);

        // Assert
        List<FireStation> fireStations = repository.fireStations;
        assertEquals(3, fireStations.size());
        assertEquals("3", fireStations.get(0).getStation());
    }


}
