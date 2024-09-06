package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.*;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FireStationService {

    @Autowired
    private FireStationRepository fireStationRepository;

    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private PersonService personService;

    @Autowired
    public FireStationService(FireStationRepository fireStationRepository, PersonService personService, MedicalRecordService medicalRecordService) {
        this.fireStationRepository = fireStationRepository;
        this.personService = personService;
        this.medicalRecordService = medicalRecordService;
    }

    public List<FireStation> getFireStations() {
        return fireStationRepository.findAll();
    }

    public CoveredPersonsListDTO createFireStationPersonsList(String stationNumber) {
        int childCounter = 0;
        int adultsCounter = 0;
        ArrayList<PersonDTO> fireStationPersonsList = new ArrayList<>();
        for (String address : fireStationRepository.getCoveredAddresses(stationNumber)) {
            for (Person person : personService.getPersons())  {
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
        for (String address : fireStationRepository.getCoveredAddresses(firestationNumber)) {
            for (Person person : personService.getPersons())  {
                if (person.getAddress().equals(address)) {
                    phoneList.add(person.getPhone());
                }
            }
        }
        return phoneList;
    }

    public PersonsListInCaseOfFireDTO createPersonsAtThisAddressList(String address) {
        String stationNumber = fireStationRepository.getStationNumber(address);
        ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = new ArrayList<>();
        for ( Person person : personService.getPersonsByAddress(address)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
            MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
            personsAtThisAddressList.add(new PersonAtThisAddressDTO(person.getLastName(), person.getPhone(), age, medicalRecordDTO));
        }
        return new PersonsListInCaseOfFireDTO(stationNumber, personsAtThisAddressList);
    }

    public List<FloodAlertDTO> createFloodAlertList(List<String> stations) {
        List<FloodAlertDTO> floodAlertList = new ArrayList<>();
        for (String station : stations) {
            for (String address : fireStationRepository.getCoveredAddresses(station)) {
                ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = new ArrayList<>();
                for ( Person person : personService.getPersonsByAddress(address)) {
                    long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
                    MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
                    MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
                    personsAtThisAddressList.add(new PersonAtThisAddressDTO(person.getLastName(), person.getPhone(), age, medicalRecordDTO));
                }
                floodAlertList.add(new FloodAlertDTO(address, personsAtThisAddressList));
            }

        }
        return floodAlertList;
    }

    public FireStation createFireStation(FireStation fireStation) throws IOException {
        return fireStationRepository.save(fireStation);
    }

    public void deleteFireStation(String address) throws IOException {
        fireStationRepository.delete(address);
    }

    public FireStation updateFireStation(FireStation fireStation) throws IOException {
        return fireStationRepository.update(fireStation);
    }
}
