package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.MedicalRecordDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private PersonRepository personRepositoryMock;

    @MockBean
    private MedicalRecordService medicalRecordService;


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
        expectedPersons.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
        expectedPersons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(expectedPersons).when(personRepositoryMock).findPersonsByLastName("Cuthbert");
        doReturn(new MedicalRecord("Marrila", "Cuthbert", "01/01/1960", new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of("")))).when(medicalRecordService).getOneMedicalRecord("Marrila", "Cuthbert");
        doReturn(new MedicalRecord("Matthew", "Cuthbert", "01/01/1955", new ArrayList<>(List.of("heartpills:100mg")), new ArrayList<>(List.of("")))).when(medicalRecordService).getOneMedicalRecord("Matthew", "Cuthbert");

        // Act
        List<PersonInfoLastNameDTO> wantedPersons = personService.getPersonsByLastName("Cuthbert");

        // Assert
        assertEquals("marrila.cuthbert@avonlea.com", wantedPersons.get(0).getMail());
        assertEquals("matthew.cuthbert@avonlea.com", wantedPersons.get(1).getMail());
        verify(personRepositoryMock).findPersonsByLastName("Cuthbert");
    }

    @Test
    void getPersonsByAddress_shouldReturnAListOfCorrectPersons() {
        // Arrange
        List<Person> expectedPersons = new ArrayList<>();
        expectedPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        expectedPersons.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
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
        personsAtThisAddress.add(new Person("Marrila", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marrila.cuthbert@avonlea.com" ));
        personsAtThisAddress.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(personsAtThisAddress).when(personRepositoryMock).findPersonByAddress("Green Gables");
        doReturn(Long.valueOf(55)).when(medicalRecordService).getAge("Matthew", "Cuthbert");
        doReturn(Long.valueOf(45)).when(medicalRecordService).getAge("Marrila", "Cuthbert");
        doReturn(Long.valueOf(13)).when(medicalRecordService).getAge("Anne", "Shirley");

        // Act
        ChildAlertDTO actualChildAlert = personService.createChildAlertList("Green Gables");

        // Assert
        assertEquals(1, actualChildAlert.getChildList().size());
        assertEquals(2, actualChildAlert.getOtherMembersList().size());
    }

}