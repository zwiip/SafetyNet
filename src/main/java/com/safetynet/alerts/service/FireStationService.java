package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.*;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FireStationService {

    @Autowired
    private FireStationRepository fireStationRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private PersonService personService;

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

    public Set<String> createPhoneList(String firestationNumber) {
        Set<String> phoneList = new HashSet<>();
        for (String address : getCoveredAddresses(firestationNumber)) {
            for (Person person : personRepository.findAll())  {
                if (person.getAddress().equals(address)) {
                    phoneList.add(person.getPhone());
                }
            }
        }
        return phoneList;
    }

    public PersonsListInCaseOfFireDTO createPersonsAtThisAddressList(String address) {
        String stationNumber = getStationNumber(address);
        ArrayList<PersonsAtThisAddressDTO> personsAtThisAddressList = new ArrayList<>();
        for ( Person person : personService.getPersonsByAddress(address)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
            MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
            personsAtThisAddressList.add(new PersonsAtThisAddressDTO(person.getLastName(), person.getPhone(), age, medicalRecordDTO));
        }
        return new PersonsListInCaseOfFireDTO(stationNumber, personsAtThisAddressList);
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

    public String getStationNumber(String address) {
        for(FireStation firesStation : getFireStations()) {
            if(firesStation.getAddress().equals(address)) {
                return firesStation.getStation();
            }
        }
        return null;
    }
}
