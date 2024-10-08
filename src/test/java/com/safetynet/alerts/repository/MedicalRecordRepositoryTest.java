package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MedicalRecordRepositoryTest {

    private MedicalRecordRepository repository;

    @BeforeEach
    public void setUp() throws IOException {
        DataRepository dataRepositoryMock = mock(DataRepository.class);

        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("./src/test/resources/dataTest.json");
        JsonNode jsonNode = new ObjectMapper().readTree(jsonFile);

        doReturn(jsonNode).when(dataRepositoryMock).getData();

        repository = new MedicalRecordRepository(dataRepositoryMock);
        JsonNode medicalRecordsNode = jsonNode.get("medicalrecords");

        TypeReference<List<MedicalRecord>> typeReferenceList = new TypeReference<>() {};
        repository.medicalRecords = objectMapper.readValue(medicalRecordsNode.traverse(), typeReferenceList);
    }

    @Test
    void findAll_shouldReturnAllMedicalRecords() {
        // Act
        List<MedicalRecord> actualMedicalRecords = repository.findAll();

        // Assert
        assertEquals(5, actualMedicalRecords.size());
        assertEquals("Anne", actualMedicalRecords.get(0).getFirstName());
        assertEquals("03/03/2010", actualMedicalRecords.get(1).getBirthdate());
    }

    @Test
    void findMedicalRecordByFullName_shouldReturnCorrectMedicalRecord() {
        // Act
        MedicalRecord wantedMedicalRecord = repository.findMedicalRecordsByFullName("Anne", "Shirley");

        // Assert
        assertEquals("Anne", wantedMedicalRecord.getFirstName());
        assertEquals("Shirley", wantedMedicalRecord.getLastName());
    }

    @Test
    void findMedicalRecordByFullName_shouldReturnNull_whenNoMatchFound() {
        // Act
        MedicalRecord wantedMedicalRecord = repository.findMedicalRecordsByFullName("Gilbert", "Blythe");

        // Assert
        assertNull(wantedMedicalRecord);
    }

    @Test
    void save_shouldAddMedicalRecordToRepository() {
        // Arrange
        MedicalRecord newMedicalRecord = new MedicalRecord("Gilbert", "Blythe", "05/09/2010", new ArrayList<>(List.of("")), new ArrayList<>(List.of("")));

        // Act
        repository.save(newMedicalRecord);

        // Assert
        assertEquals(6, repository.medicalRecords.size());
        assertTrue(repository.medicalRecords.contains(newMedicalRecord));
    }

    @Test
    void delete_shouldRemoveMedicalRecordFromRepository() {
        // Arrange
        MedicalRecord medicalRecordToDelete = new MedicalRecord("Anne", "Shirley", "01/02/2010", new ArrayList<>(List.of("")), new ArrayList<>(List.of("")));

        // Act
        repository.delete(medicalRecordToDelete);

        // Assert
        assertFalse(repository.medicalRecords.contains(medicalRecordToDelete));
        assertEquals(4, repository.medicalRecords.size());
    }

    @Test
    void update_shouldModifyExistingMedicalRecord() {
        // Arrange
        MedicalRecord medicalRecordToUpdate = new MedicalRecord("Anne", "Shirley", "01/02/2000", new ArrayList<>(List.of("")), new ArrayList<>(List.of("")));

        // Act
        repository.update(medicalRecordToUpdate);

        // Assert
        assertTrue(repository.medicalRecords.contains(medicalRecordToUpdate));
        assertEquals(5, repository.medicalRecords.size());
        assertEquals("01/02/2000", repository.medicalRecords.getFirst().getBirthdate());
    }
}