package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PersonRepositoryTest {

    private PersonRepository repository;

    @BeforeEach
    public void setUp() throws IOException {
        DataRepository dataRepositoryMock = mock(DataRepository.class);

        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("./src/test/resources/dataTest.json");
        JsonNode jsonNode = new ObjectMapper().readTree(jsonFile);

        doReturn(jsonNode).when(dataRepositoryMock).getData();

        repository = new PersonRepository(dataRepositoryMock);
        JsonNode personsNode = jsonNode.get("persons");

        TypeReference<List<Person>> typeReferenceList = new TypeReference<>() {};
        repository.persons = objectMapper.readValue(personsNode.traverse(), typeReferenceList);
    }

    @Test
    void findAll_shouldReturnAllPersons() {
        // Act
        List<Person> persons = repository.findAll();

        // Assert
        assertEquals(5, persons.size());
        assertEquals("Anne", persons.get(0).getFirstName());
        assertEquals("Diana", persons.get(1).getFirstName());
    }

    @Test
    void findPersonByFullName_shouldReturnWantedPerson() {
        // Act
        Person wantedPerson = repository.findPersonByFullName("Anne", "Shirley");

        // Assert
        assertNotNull(wantedPerson);
        assertEquals("Anne", wantedPerson.getFirstName());
        assertEquals("Shirley", wantedPerson.getLastName());
    }

    @Test
    void findPersonByFullName_shouldReturnNull_whenNoPersonFound() {
        // Act
        Person wantedPerson = repository.findPersonByFullName("Gilbert", "Blythe");

        // Assert
        assertNull(wantedPerson);
    }

    @Test
    void findPersonByLastName_shouldReturnAListOfCorrectPersons() {
        // Act
        List<Person> wantedPersonsList = repository.findPersonsByLastName("Cuthbert");

        // Assert
        assertEquals(2, wantedPersonsList.size());
        assertEquals("Marilla", wantedPersonsList.getFirst().getFirstName());
    }

    @Test
    void findPersonsByAddress_shouldReturnAListOfCorrectPersons() {
        // Act
        List<Person> wantedPersonsList = repository.findPersonByAddress("Green Gables");

        // Assert
        assertEquals(3, wantedPersonsList.size());
        assertEquals("Anne", wantedPersonsList.getFirst().getFirstName());
    }

    @Test
    void findPersonsByCity_shouldReturnAListOfCorrectPersons() {
        // Act
        List<Person> wantedPersonsList = repository.findPersonsByCity("Avonlea");

        // Assert
        assertEquals(4, wantedPersonsList.size());
        assertEquals("Anne", wantedPersonsList.getFirst().getFirstName());
    }

    @Test
    void save_shouldAddPersonToRepository() {
        // Arrange
        Person newPerson = new Person("Gilbert", "Blythe", "Old Farm", "Avonlea", "12345", "1598476321", "gilbert.blythe@avonlea.com");

        // Act
        repository.save(newPerson);

        // Assert
        assertTrue(repository.persons.contains(newPerson));
        assertEquals(6, repository.persons.size());
    }

    @Test
    void delete_shouldRemovePersonFromRepository() {
        // Arrange
        Person personToDelete = new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com");

        // Act
        repository.delete(personToDelete);

        // Assert
        assertFalse(repository.persons.contains(personToDelete));
        assertEquals(4, repository.persons.size());
    }

    @Test
    void update_shouldModifyExistingPerson() {
        // Arrange
        Person updatedPerson = new Person("Anne", "Shirley", "Patty's House", "Redmond", "74185", "0123456789", "anne.shirley@avonlea.com");

        // Act
        repository.update(updatedPerson);

        // Assert
        assertTrue(repository.persons.contains(updatedPerson));
        assertEquals(5, repository.persons.size());
        assertEquals("Patty's House", repository.persons.getFirst().getAddress());
        assertEquals("Redmond", repository.persons.getFirst().getCity());
    }
}
