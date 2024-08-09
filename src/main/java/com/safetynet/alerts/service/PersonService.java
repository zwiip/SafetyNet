package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.FullNameAndAgeDTO;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordService medicalRecordService;

    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    public Person getOnePerson(String firstName, String lastName) {
        return personRepository.findPersonByFullName(firstName, lastName);
    }

    public List<Person> getPersonsByAddress(String address) {
        return personRepository.findPersonByAddress(address);
    }

    public ChildAlertDTO createChildAlertList(String address) {
        ArrayList<FullNameAndAgeDTO> adultsList = new ArrayList<>();
        ArrayList<FullNameAndAgeDTO> childrenList = new ArrayList<>();
        for (Person person : getPersonsByAddress(address)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            if (age <= 18) {
                childrenList.add(new FullNameAndAgeDTO(person.getFirstName(), person.getLastName(), age));
            } else {
                adultsList.add(new FullNameAndAgeDTO(person.getFirstName(), person.getLastName(), age));
            }
        }
        return new ChildAlertDTO(childrenList, adultsList);
    }

}