package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    private PersonService personService;

    @Mock
    private PersonRepository personRepositoryMock;

    @Mock
    private MedicalRecordService medicalRecordServiceMock;

    @BeforeEach
    public void setUp() {
        personService = new PersonService(personRepositoryMock, medicalRecordServiceMock);
    }

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

    @Test
    void getPersonsByLastName_shouldReturnALisOfCorrectPersons() {
        // Arrange
        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        expectedPersons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(expectedPersons).when(personRepositoryMock).findPersonsByLastName("Cuthbert");
        doReturn(new MedicalRecord("Marilla", "Cuthbert", "01/01/1960", new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Marilla", "Cuthbert");
        doReturn(new MedicalRecord("Matthew", "Cuthbert", "01/01/1955", new ArrayList<>(List.of("heartpills:100mg")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Matthew", "Cuthbert");

        // Act
        List<PersonInfoLastNameDTO> wantedPersons = personService.getPersonsByLastName("Cuthbert");

        // Assert
        assertEquals("marilla.cuthbert@avonlea.com", wantedPersons.get(0).getMail());
        assertEquals("matthew.cuthbert@avonlea.com", wantedPersons.get(1).getMail());
        verify(personRepositoryMock).findPersonsByLastName("Cuthbert");
    }

    @Test
    void getPersonsByAddress_shouldReturnAListOfCorrectPersons() {
        // Arrange
        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        expectedPersons.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        expectedPersons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(expectedPersons).when(personRepositoryMock).findPersonByAddress("Green Gables");

        // Act
        List<Person> wantedPersons = personService.getPersonsByAddress("Green Gables");

        // Assert
        assertEquals(expectedPersons, wantedPersons);
        verify(personRepositoryMock).findPersonByAddress("Green Gables");
    }

    @Test
    void createChildAlertList_shouldReturnAChildAlertDTO() {
        // Arrange
        List<Person> personsAtThisAddress = new ArrayList<>();
        personsAtThisAddress.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        personsAtThisAddress.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        personsAtThisAddress.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(personsAtThisAddress).when(personRepositoryMock).findPersonByAddress("Green Gables");
        doReturn(55L).when(medicalRecordServiceMock).getAge("Matthew", "Cuthbert");
        doReturn(45L).when(medicalRecordServiceMock).getAge("Marilla", "Cuthbert");
        doReturn(13L).when(medicalRecordServiceMock).getAge("Anne", "Shirley");

        // Act
        ChildAlertDTO actualChildAlert = personService.createChildAlertList("Green Gables");

        // Assert
        assertEquals(1, actualChildAlert.getChildList().size());
        assertEquals(2, actualChildAlert.getOtherMembersList().size());
        verify(personRepositoryMock).findPersonByAddress("Green Gables");
        verify(medicalRecordServiceMock).getAge("Anne", "Shirley");
    }

    @Test
    void getPersonEmails_shouldReturnAListOfEmails() {
        // Arrange
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        persons.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        persons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(persons).when(personRepositoryMock).findPersonsByCity("Avonlea");

        // Act
        List<String> emails = personService.getPersonsEmails("Avonlea");

        // Assert
        assertEquals("anne.shirley@avonlea.com", emails.get(0));
        assertEquals("marilla.cuthbert@avonlea.com", emails.get(2));
        verify(personRepositoryMock).findPersonsByCity("Avonlea");
    }
}