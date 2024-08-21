package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepositoryMock;

    @InjectMocks
    private PersonService personService;

    @Test
    void getPersons_shouldReturnListOfAllPersonsFromRepository() {
        // Arrange
        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        expectedPersons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));

        when(personRepositoryMock.findAll()).thenReturn(expectedPersons);

        // Act
        List<Person> actualPersons = personService.getPersons();

        // Assert
        assertEquals(expectedPersons, actualPersons);
        verify(personRepositoryMock).findAll();
    }

    @Test
    void getOnePerson_shouldReturnTheCorrectPerson() {
        // Arrange
        Person expectedPerson = new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" );

        when(personRepositoryMock.findPersonByFullName("Anne", "Shirley")).thenReturn(expectedPerson);

        // Act
        Person wantedPerson = personService.getOnePerson("Anne", "Shirley");

        // Assert
        assertEquals(expectedPerson, wantedPerson);
        verify(personRepositoryMock).findPersonByFullName("Anne", "Shirley");
    }

}