package com.safetynet.alerts.service;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String createFireStationPersonsList(String stationNumber) {
        int childCounter = 0;
        int adultsCounter = 0;
        ArrayList<Map<String, String>> fireStationPersonsList = new ArrayList<>();
        for (String address : getCoveredAddresses(stationNumber)) {
            for (Person person : personRepository.findAll())  {
                if (person.getAddress().equals(address)) {
                    if (medicalRecordService.isChild(person.getFirstName(), person.getLastName())) {
                        childCounter++;
                    } else {
                        adultsCounter++;
                    }
                    Map<String, String> fireStationPersonsListPerson = new HashMap<>();
                    fireStationPersonsListPerson.put("firstName", person.getFirstName());
                    fireStationPersonsListPerson.put("lastName", person.getLastName());
                    fireStationPersonsListPerson.put("address", person.getAddress());
                    fireStationPersonsListPerson.put("phone", person.getPhone());
                    fireStationPersonsList.add(fireStationPersonsListPerson);
                }
            }
        }

        return "Pour la station n°" + stationNumber +
                "\n le nombre d'enfants est de " + childCounter +
                "\n le nombre d'adultes est de " + adultsCounter +
                "\n " + fireStationPersonsList;
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
