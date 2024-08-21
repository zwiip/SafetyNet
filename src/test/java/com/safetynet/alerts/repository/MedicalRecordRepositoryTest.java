package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MedicalRecordRepositoryTest {
    private MedicalRecordRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new MedicalRecordRepository();
    }

    @Test
    void createListMedicalRecords_shouldPopulateMedicalRecordsList() throws IOException {
        // Arrange
        String jsonContent = "{ \"medicalrecords\": [" +
                "{ \"firstName\":\"John\", \"lastName\":\"Boyd\", \"birthdate\":\"03/06/1984\", \"medications\":[\"aznol:350mg\", \"hydrapermazol:100mg\"], \"allergies\":[\"nillacilan\"] }," +
                "{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"birthdate\":\"03/06/1989\", \"medications\":[\"pharmacol:5000mg\", \"terazine:10mg\", \"noznazol:250mg\"], \"allergies\":[] }" +
                "] }";
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        // Act
        repository.createListMedicalRecords(jsonNode);

        // Assert
        List<MedicalRecord> medicalRecords = repository.findAll();
        assertEquals(2, medicalRecords.size());
        assertEquals("John", medicalRecords.get(0).getFirstName());
        assertEquals("Jacob", medicalRecords.get(1).getFirstName());
    }

    @Test
    void findAll_shouldReturnAllMedicalRecords() {
        // Arrange
        List<MedicalRecord> expectedMedicalRecords = new ArrayList<>();
        expectedMedicalRecords.add(new MedicalRecord("Roger", "Boyd", "09/06/2017", new ArrayList<>(List.of("")), new ArrayList<>(List.of(""))));
        expectedMedicalRecords.add(new MedicalRecord("Felicia", "Boyd", "01/08/1986", new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal"))));

        repository.medicalRecords = expectedMedicalRecords;

        // Act
        List<MedicalRecord> actualMedicalRecords = repository.findAll();

        // Assert
        assertEquals(expectedMedicalRecords, actualMedicalRecords);
    }

    @Test
    void findMedicalRecordByFullName_shouldReturnCorrectMedicalRecord() {
        // Arrange
        List<MedicalRecord> medicalRecordList = new ArrayList<>();
        medicalRecordList.add(new MedicalRecord("Roger", "Boyd", "09/06/2017", new ArrayList<>(List.of("")), new ArrayList<>(List.of(""))));
        medicalRecordList.add(new MedicalRecord("Felicia", "Boyd", "01/08/1986", new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal"))));

        repository.medicalRecords = medicalRecordList;

        // Act
        MedicalRecord wantedMedicalRecord = repository.findMedicalRecordsByFullName("Felicia", "Boyd");

        // Assert
        assertNotNull(wantedMedicalRecord);
        assertEquals("Felicia", wantedMedicalRecord.getFirstName());
        assertEquals("Boyd", wantedMedicalRecord.getLastName());
    }

    @Test
    void findMedicalRecordByFullName_shouldReturnNull_whenNoMatchFound() {
        // Arrange
        List<MedicalRecord> medicalRecordList = new ArrayList<>();
        medicalRecordList.add(new MedicalRecord("Roger", "Boyd", "09/06/2017", new ArrayList<>(List.of("")), new ArrayList<>(List.of(""))));
        medicalRecordList.add(new MedicalRecord("Felicia", "Boyd", "01/08/1986", new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal"))));

        repository.medicalRecords = medicalRecordList;

        // Act
        MedicalRecord wantedMedicalRecord = repository.findMedicalRecordsByFullName("Anne", "Shirley");

        // Assert
        assertNull(wantedMedicalRecord);
    }
}
