package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.CoveredPersonsListDTO;
import com.safetynet.alerts.controller.dto.PersonDTO;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FireStationService {

    @Autowired
    private FireStationRepository fireStationRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private MedicalRecordService medicalRecordService;

    public List<FireStation> getFireStations() {
        return fireStationRepository.findAll();
    }

    public CoveredPersonsListDTO createFireStationPersonsList(String stationNumber) {
        int childCounter = 0;
        int adultsCounter = 0;
        ArrayList<PersonDTO> fireStationPersonsList = new ArrayList<>();
        for (String address : getCoveredAddresses(stationNumber)) {
            for (Person person : personRepository.findAll())  {
                if (person.getAddress().equals(address)) {
                    if (medicalRecordService.isChild(person.getFirstName(), person.getLastName())) {
                        childCounter++;
                    } else {
                        adultsCounter++;
                    }
                    fireStationPersonsList.add(new PersonDTO(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()));
                }
            }
        }
        return new CoveredPersonsListDTO(childCounter, adultsCounter, fireStationPersonsList);
    }

    public ArrayList<String> createPhoneList(String firestationNumber) {
        ArrayList<String> phoneList = new ArrayList<>();
        for (String address : getCoveredAddresses(firestationNumber)) {
            for (Person person : personRepository.findAll())  {
                if (person.getAddress().equals(address)) {
                    phoneList.add(person.getPhone());
                }
            }
        }
        return phoneList;
    }

    public ArrayList<String> getCoveredAddresses(String stationNumber) {
        ArrayList<String> coveredAddresses = new ArrayList<>();

        for(FireStation firesStation : getFireStations()) {
            if(firesStation.getStation().equals(stationNumber)) {
                coveredAddresses.add(firesStation.getAddress());
            }
        }
        return coveredAddresses;
    }
}
