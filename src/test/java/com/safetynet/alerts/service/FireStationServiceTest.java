package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.*;
import com.safetynet.alerts.model.FireStation;
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
    void createFloodAlertList_shouldReturnAListofFloodAlertDTO() {
        // Arrange
        List<String> coveredAddress = new ArrayList<>();
        coveredAddress.add("Green Gables");
        coveredAddress.add("Orchard Slope");
        doReturn(coveredAddress).when(fireStationRepositoryMock).getCoveredAddresses("1");

        List<PersonAtThisAddressDTO> greenGablesPersons = new ArrayList<>();
        greenGablesPersons.add(new PersonAtThisAddressDTO("Shirley", "0123456789", 13L,  new MedicalRecordDTO(new ArrayList<>(List.of("")), new ArrayList<>(List.of("")))));
        greenGablesPersons.add(new PersonAtThisAddressDTO("Cuthbert", "0123456789", 55L,  new MedicalRecordDTO(new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of("")))));
        doReturn(greenGablesPersons).when(personServiceMock).createPersonsAtThisAddressList("Green Gables");

        List<PersonAtThisAddressDTO> orchardSlopePersons = new ArrayList<>();
        orchardSlopePersons.add(new PersonAtThisAddressDTO("Barry", "9876543210", 14L,  new MedicalRecordDTO(new ArrayList<>(List.of("")), new ArrayList<>(List.of("dust")))));
        doReturn(orchardSlopePersons).when(personServiceMock).createPersonsAtThisAddressList("Orchard Slope");

        // Act
        List<FloodAlertDTO> floodAlertDTOList = fireStationService.createFloodAlertList(List.of("1"));

        // Assert
        assertEquals(2, floodAlertDTOList.size());
        assertEquals("Green Gables", floodAlertDTOList.get(0).getAddress());
        assertEquals("Orchard Slope", floodAlertDTOList.get(1).getAddress());
    }

}
