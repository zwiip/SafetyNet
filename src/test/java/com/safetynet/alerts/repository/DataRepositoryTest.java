import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataRepositoryTest {

    private DataRepository dataRepository;
    private ObjectMapper objectMapperMock;

    @BeforeEach
    void setUp() {
        objectMapperMock = mock(ObjectMapper.class);
        dataRepository = new DataRepository();
        dataRepository.objectMapper = objectMapperMock;
    }

    @Test
    void getData_shouldReturnJsonNode_whenFileIsValid() throws IOException {
        // Arrange
        File fileMock = mock(File.class);
        JsonNode jsonNodeMock = mock(JsonNode.class);

        when(objectMapperMock.readTree(any(File.class))).thenReturn(jsonNodeMock);

        // Act
        JsonNode result = dataRepository.getData();

        // Assert
        assertNotNull(result);
        verify(objectMapperMock).readTree(any(File.class));
    }

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

}