package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.FloodAlertDTO;
import com.safetynet.alerts.controller.dto.PersonsListInCaseOfFireDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

    private FireStationService fireStationService;

    @Mock
    private FireStationRepository fireStationRepositoryMock;

    @Mock
    private PersonService personServiceMock;

    @Mock
    private MedicalRecordService medicalRecordServiceMock;

    @BeforeEach
    public void setUp() {
        fireStationService = new FireStationService(fireStationRepositoryMock, personServiceMock, medicalRecordServiceMock);
    }

    @Test
    void getFireStations_shouldReturnAllFireStations() {
        // Arrange
        List<FireStation> expectedFireStations = new ArrayList<>();
        expectedFireStations.add(new FireStation("Green Gables", "1"));
        expectedFireStations.add(new FireStation("Orchard Slope", "1"));
        expectedFireStations.add(new FireStation("10 Great Avenue", "2"));
        expectedFireStations.add(new FireStation("28 Poppy Lane", "2"));

        doReturn(expectedFireStations).when(fireStationRepositoryMock).findAll();

        // Act
        List<FireStation> actualFireStations = fireStationService.getFireStations();

        // Assert
        assertEquals(expectedFireStations, actualFireStations);
        verify(fireStationRepositoryMock).findAll();
    }

    @Test
    void createFireStationPersonsList_shouldReturnACoveredPersonsListDTO() {
        // Arrange
        List<String> coveredAddress = new ArrayList<>();
        coveredAddress.add("Green Gables");
        coveredAddress.add("Orchard Slope");

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        persons.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        persons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(coveredAddress).when(fireStationRepositoryMock).getCoveredAddresses("1");

        doReturn(persons).when(personServiceMock).getPersons();

        doReturn(true).when(medicalRecordServiceMock).isChild("Anne", "Shirley");
        doReturn(true).when(medicalRecordServiceMock).isChild("Diana", "Barry");
        doReturn(false).when(medicalRecordServiceMock).isChild("Marilla", "Cuthbert");
        doReturn(false).when(medicalRecordServiceMock).isChild("Matthew", "Cuthbert");

        // Act
        CoveredPersonsListDTO coveredPersonsListDTO = fireStationService.createFireStationPersonsList("1");

        // Assert
        assertNotEquals(0, coveredPersonsListDTO.getCoveredPersons().size());
        assertEquals(2, coveredPersonsListDTO.getChildCount());
        assertEquals(2, coveredPersonsListDTO.getAdultsCount());
    }

    @Test
    void createPhoneList_shouldReturnASetOfPhoneNumbers() {
        // Arrange
        List<String> coveredAddress = new ArrayList<>();
        coveredAddress.add("Green Gables");
        coveredAddress.add("Orchard Slope");

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        persons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        persons.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        persons.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));
        persons.add(new Person("Josephine", "Barry","Big House", "Charlottetown", "54321", "135798642", "josephine.barry@aunt.com" ));

        doReturn(persons).when(personServiceMock).getPersons();
        doReturn(coveredAddress).when(fireStationRepositoryMock).getCoveredAddresses("1");

        // Act
        Set<String> phoneList = fireStationService.createPhoneList("1");

        // Assert
        assertEquals(2, phoneList.size());
        verify(personServiceMock, times(2)).getPersons();
    }

    @Test
    void createPersonsAtThisAddressList_shouldReturnAPersonsListInCaseOfFireInCaseOfFireDTO() {
        doReturn("1").when(fireStationRepositoryMock).getStationNumber("Green Gables");

        List<Person> personsAtThisAddres = new ArrayList<>();
        personsAtThisAddres.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        personsAtThisAddres.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        personsAtThisAddres.add(new Person("Matthew", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "matthew.cuthbert@avonlea.com" ));

        doReturn(personsAtThisAddres).when(personServiceMock).getPersonsByAddress("Green Gables");

        doReturn(13L).when(medicalRecordServiceMock).getAge("Anne", "Shirley");
        doReturn(45L).when(medicalRecordServiceMock).getAge("Marilla", "Cuthbert");
        doReturn(55L).when(medicalRecordServiceMock).getAge("Matthew", "Cuthbert");

        doReturn(new MedicalRecord("Anne", "Shirley", "01/01/2011", new ArrayList<>(List.of("")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Anne", "Shirley");
        doReturn(new MedicalRecord("Marilla", "Cuthbert", "01/01/1960", new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Marilla", "Cuthbert");
        doReturn(new MedicalRecord("Matthew", "Cuthbert", "01/01/1955", new ArrayList<>(List.of("heartpills:100mg")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Matthew", "Cuthbert");

        // Act
        PersonsListInCaseOfFireDTO personsListInCaseOfFireDTO = fireStationService.createPersonsListInCaseOfFire("Green Gables");

        // Assert
        assertEquals(3, personsListInCaseOfFireDTO.getPersonsAtThisAddress().size());
    }

    @Test
    void createFloodAlertList_shouldReturnAListofFloodAlertDTO() {
        List<String> coveredAddress = new ArrayList<>();
        coveredAddress.add("Green Gables");
        coveredAddress.add("Orchard Slope");
        doReturn(coveredAddress).when(fireStationRepositoryMock).getCoveredAddresses("1");

        List<Person> greenGablesPersons = new ArrayList<>();
        greenGablesPersons.add(new Person("Anne", "Shirley", "Green Gables", "Avonlea", "12345", "0123456789", "anne.shirley@avonlea.com" ));
        greenGablesPersons.add(new Person("Marilla", "Cuthbert", "Green Gables", "Avonlea", "12345", "0123456789", "marilla.cuthbert@avonlea.com" ));
        doReturn(greenGablesPersons).when(personServiceMock).getPersonsByAddress("Green Gables");

        List<Person> orchardSlopePersons = new ArrayList<>();
        orchardSlopePersons.add(new Person("Diana", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "diana.barry@avonlea.com" ));
        orchardSlopePersons.add(new Person("George", "Barry","Orchard Slope", "Avonlea", "12345", "0987654321", "george.barry@avonlea.com" ));
        doReturn(orchardSlopePersons).when(personServiceMock).getPersonsByAddress("Orchard Slope");

        doReturn(13L).when(medicalRecordServiceMock).getAge("Anne", "Shirley");
        doReturn(13L).when(medicalRecordServiceMock).getAge("Diana", "Barry");
        doReturn(45L).when(medicalRecordServiceMock).getAge("George", "Barry");
        doReturn(55L).when(medicalRecordServiceMock).getAge("Marilla", "Cuthbert");

        doReturn(new MedicalRecord("Anne", "Shirley", "01/01/2011", new ArrayList<>(List.of("")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Anne", "Shirley");
        doReturn(new MedicalRecord("Marilla", "Cuthbert", "01/01/1960", new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Marilla", "Cuthbert");
        doReturn(new MedicalRecord("Diana", "Barry", "01/01/2011", new ArrayList<>(List.of("heartpills:100mg")), new ArrayList<>(List.of("")))).when(medicalRecordServiceMock).getOneMedicalRecord("Diana", "Barry");
        doReturn(new MedicalRecord("George", "Barry", "01/01/1950", new ArrayList<>(List.of("doliprane:500mg")), new ArrayList<>(List.of("bees", "strawberries")))).when(medicalRecordServiceMock).getOneMedicalRecord("George", "Barry");

        // Act
        List<FloodAlertDTO> floodAlertDTOList = fireStationService.createFloodAlertList(List.of("1"));

        // Assert
        assertEquals(2, floodAlertDTOList.size());
    }
}
