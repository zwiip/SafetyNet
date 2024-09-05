package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    private MedicalRecordService medicalRecordService;

    @Mock
    private MedicalRecordRepository medicalRecordRepositoryMock;

    @BeforeEach
    public void setUp() {
        medicalRecordService = new MedicalRecordService(medicalRecordRepositoryMock);
    }

    @Test
    public void getMedicalRecords_shouldReturnAllMedicalRecords() {
        // Arrange
        List<MedicalRecord> expectedMedicalRecords = new ArrayList<>();
        expectedMedicalRecords.add(new MedicalRecord("Roger", "Boyd", "09/06/2017", new ArrayList<>(List.of("")), new ArrayList<>(List.of(""))));
        expectedMedicalRecords.add(new MedicalRecord("Felicia", "Boyd", "01/08/1986", new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal"))));

        doReturn(expectedMedicalRecords).when(medicalRecordRepositoryMock).findAll();

        // Act
        List<MedicalRecord> actualMedicalRecords = medicalRecordService.getMedicalRecords();

        // Assert
        assertEquals(expectedMedicalRecords, actualMedicalRecords);
        verify(medicalRecordRepositoryMock).findAll();
    }

    @Test
    void getAge_shouldReturnAge() {
        // Arrange
        int age = 40;
        LocalDate today = LocalDate.now();
        LocalDate birthDate = today.minusYears(age);
        String birthDateString = birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE));

        doReturn(new MedicalRecord("Roger", "Boyd", birthDateString, new ArrayList<>(List.of("")), new ArrayList<>(List.of("")))).when(medicalRecordRepositoryMock).findMedicalRecordsByFullName("Roger", "Boyd");

        // Act
        Long actualAge = medicalRecordService.getAge("Roger", "Boyd");

        // Assert
        assertNotNull(age);
        assertEquals(Long.valueOf(age), actualAge);
    }

    @Test
    void isChild_shouldReturnTrue_whenAgeIsUnder18() {
        // Arrange
        int age = 7;
        LocalDate today = LocalDate.now();
        LocalDate birthDate = today.minusYears(age);
        String birthDateString = birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE));

        doReturn(new MedicalRecord("Roger", "Boyd", birthDateString, new ArrayList<>(List.of("")), new ArrayList<>(List.of("")))).when(medicalRecordRepositoryMock).findMedicalRecordsByFullName("Roger", "Boyd");

        // Act
        boolean isChild = medicalRecordService.isChild("Roger", "Boyd");

        // Assert
        assertTrue(isChild);
    }

    @Test
    void isChild_shouldReturnTrue_whenAgeEquals18() {
        // Arrange
        int age = 18;
        LocalDate today = LocalDate.now();
        LocalDate birthDate = today.minusYears(age);
        String birthDateString = birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE));

        doReturn(new MedicalRecord("Felicia", "Boyd", birthDateString, new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal")))).when(medicalRecordRepositoryMock).findMedicalRecordsByFullName("Felicia", "Boyd");

        // Act
        boolean isChild = medicalRecordService.isChild("Felicia", "Boyd");

        // Assert
        assertTrue(isChild);
    }

    @Test
    void isChild_shouldReturnFalse_whenAgeIsGreaterThan18() {
        // Arrange
        int age = 40;
        LocalDate today = LocalDate.now();
        LocalDate birthDate = today.minusYears(age);
        String birthDateString = birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE));

        doReturn(new MedicalRecord("Felicia", "Boyd", birthDateString, new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal")))).when(medicalRecordRepositoryMock).findMedicalRecordsByFullName("Felicia", "Boyd");

        // Act
        boolean isChild = medicalRecordService.isChild("Felicia", "Boyd");

        // Assert
        assertFalse(isChild);
    }
}
