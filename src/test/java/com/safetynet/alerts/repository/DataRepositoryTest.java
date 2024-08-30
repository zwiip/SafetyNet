package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataRepositoryTest {

    private DataRepository dataRepository;

    @Test
    void getData_shouldReturnJsonNode_whenFileIsValid() throws IOException {
        // Arrange
        dataRepository = new DataRepository("./src/test/resources/dataTest.json");

        // Act
        JsonNode result = dataRepository.getData();

        // Assert
        assertNotNull(result);
    }

    /*
    @Test
    void getData_shouldThrowRuntimeException_whenFileDoesNotExist() throws IOException {
        // Arrange
        when(objectMapperMock.readTree(any(File.class))).thenThrow(new IOException("File not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> dataRepository.getData());
    }

    @Test
    void getData_shouldThrowRuntimeException_whenIOExceptionOccurs() throws IOException {
        // Arrange
        when(objectMapperMock.readTree(any(File.class))).thenThrow(new IOException("Error reading file"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> dataRepository.getData());
    }
    */


}