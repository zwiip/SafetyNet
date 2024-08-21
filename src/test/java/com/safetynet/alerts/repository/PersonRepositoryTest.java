package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonRepositoryTest {
    private PersonRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new PersonRepository();
    }

    @Test
    void createListPersons_shouldPopulatePersonList() throws IOException {
        // Arrange
        String jsonContent = "{ \"persons\": [" +
                "{\"firstName\": \"Anne\", \"lastName\": \"Shirley\", \"address\": \"Green Gables\", \"city\": \"Avonlea\", \"zip\": \"12345\", \"phone\": \"0123456789\", \"email\": \"anne.shirley@avonlea.com\"}," +
                "{\"firstName\": \"Diana\", \"lastName\": \"Barry\", \"address\": \"Orchard Slope\", \"city\": \"Avonlea\", \"zip\": \"12345\", \"phone\": \"0987654321\", \"email\": \"diana.barry@avonlea.com\"}" +
                "] }";
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        // Act
        repository.createListPersons(jsonNode);

        // Assert
        List<Person> persons = repository.findAll();
        assertEquals(2, persons.size());
        assertEquals("Anne", persons.get(0).getFirstName());
        assertEquals("Diana", persons.get(1).getFirstName());
    }

    @Test
    void findAll_shouldReturnAllPersons() {
        // Arrange
        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        expectedPersons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));

        repository.persons = expectedPersons;

        // Act
        List<Person> actualPersons = repository.findAll();

        // Assert
        assertEquals(expectedPersons, actualPersons);
    }

    @Test
    void findPersonByFullName_shouldReturnWantedPerson() {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));

        repository.persons = persons;

        // Act
        Person wantedPerson = repository.findPersonByFullName("Anne", "Shirley");

        // Assert
        assertNotNull(wantedPerson);
        assertEquals("Anne", wantedPerson.getFirstName());
        assertEquals("Shirley", wantedPerson.getLastName());
    }

    @Test
    void findPersonByFullName_shouldReturnNull_whenNoPersonFound() {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));

        repository.persons = persons;

        // Act
        Person wantedPerson = repository.findPersonByFullName("Marrila", "Cuthbert");

        // Assert
        assertNull(wantedPerson);
    }

    @Test
    void findPersonByLastName_shouldReturnAListOfCorrectPersons() {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        persons.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
        persons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
        expectedPersons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        repository.persons = persons;

        // Act
        List<Person> wantedPersonsList = repository.findPersonsByLastName("Cuthbert");

        // Assert
        assertNotNull(wantedPersonsList);
        assertEquals(expectedPersons.toString(), wantedPersonsList.toString());
    }

    @Test
    void findPersonsByAddress_shouldReturnAListOfCorrectPersons() {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        persons.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
        persons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        repository.persons = persons;

        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        expectedPersons.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
        expectedPersons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        // Act
        List<Person> wantedPersonsList = repository.findPersonByAddress("Green Gables");

        // Assert
        assertNotNull(wantedPersonsList);
        assertEquals(expectedPersons.toString(), wantedPersonsList.toString());
    }

    @Test
    void findPersonsByCity_shouldReturnAListOfCorrectPersons() {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        persons.add(new Person("Josephine", "Barry","Big House", "Charlottetown", "54321", "135798642", "josephine.barry@aunt.com" ));

        repository.persons = persons;

        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        expectedPersons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));

        // Act
        List<Person> wantedPersonsList = repository.findPersonsByCity("Avonlea");

        // Assert
        assertNotNull(wantedPersonsList);
        assertEquals(expectedPersons.toString(), wantedPersonsList.toString());
    }

}
