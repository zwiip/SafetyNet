package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.FullNameAndAgeDTO;
import com.safetynet.alerts.controller.dto.MedicalRecordDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    public PersonService(PersonRepository personRepository, MedicalRecordService medicalRecordService) {
        this.personRepository = personRepository;
        this.medicalRecordService = medicalRecordService;
    }

    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    public Person getOnePerson(String firstName, String lastName) {
        return personRepository.findPersonByFullName(firstName, lastName);
    }
    
    public List<PersonInfoLastNameDTO> getPersonsByLastName(String lastName) {
        List<PersonInfoLastNameDTO> personInfoLastNameDTOList = new ArrayList<>();
        for (Person person : personRepository.findPersonsByLastName(lastName)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
            MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
            personInfoLastNameDTOList.add(new PersonInfoLastNameDTO(person.getLastName(), person.getAddress(), age, person.getEmail(), medicalRecordDTO));
        }
        return personInfoLastNameDTOList;
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

    public List<String> getPersonsEmails(String city) {
        ArrayList<String> emails = new ArrayList<>();
        for (Person person : personRepository.findPersonsByCity(city)) {
            emails.add(person.getEmail());
        }
        return emails;
    }

    public Person createPerson(Person person) throws IOException {
        Person personAdded = personRepository.save(person);
        return personAdded;
    }

    public void deleteOnePerson(String firstName, String lastName) throws IOException {
        personRepository.delete(firstName, lastName);
    }

    public Person updatePerson(Person person) throws IOException {
        Person personUpdated = personRepository.update(person);
        return personUpdated;
    }
}