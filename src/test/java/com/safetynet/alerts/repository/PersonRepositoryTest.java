package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PersonRepositoryTest {
    private PersonRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new PersonRepository();
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
}
