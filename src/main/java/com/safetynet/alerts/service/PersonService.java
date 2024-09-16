package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.FullNameAndAgeDTO;
import com.safetynet.alerts.controller.dto.MedicalRecordDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    private final MedicalRecordService medicalRecordService;

    /* CONSTRUCTOR */
    public PersonService(PersonRepository personRepository, MedicalRecordService medicalRecordService) {
        this.personRepository = personRepository;
        this.medicalRecordService = medicalRecordService;
    }

    /* METHODS */
    /**
     * Retrieves the list of all persons.
     * @return a list of Person objects.
     */
    public List<Person> getPersons() {
        logger.debug("Retrieving all persons");
        return personRepository.findAll();
    }

    /**
     * Retrieve a person matching the inputs.
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return a Person object.
     */
    public Person getOnePerson(String firstName, String lastName) {
        logger.debug("Retrieving {} {}", firstName, lastName);
        return personRepository.findPersonByFullName(firstName, lastName);
    }

    /**
     * Creates a list of persons matching the given last name.
     * @param lastName a String representing the last name of the persons we are looking for.
     * @return a List of PersonInfoLastNameDTO object containing the details and medical record of each person matching the name.
     */
    public List<PersonInfoLastNameDTO> getPersonsByLastName(String lastName) {
        logger.debug("Retrieving persons by last name: {}", lastName);
        List<PersonInfoLastNameDTO> personInfoLastNameDTOList = new ArrayList<>();
        for (Person person : personRepository.findPersonsByLastName(lastName)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
            MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
            personInfoLastNameDTOList.add(new PersonInfoLastNameDTO(person.getLastName(), person.getAddress(), age, person.getEmail(), medicalRecordDTO));
        }
        logger.info("Created a list of {} persons matching the name {}", personInfoLastNameDTOList.size(), lastName);
        return personInfoLastNameDTOList;
    }

    /**
     * Creating a list of persons living at the given address.
     * @param address a String representing the address.
     * @return a List of Person objects living at the given address.
     */
    public List<Person> getPersonsByAddress(String address) {
        logger.debug("Retrieving a list of persons by address: {}", address);
        return personRepository.findPersonByAddress(address);
    }

    /**
     * Create an object containing the lists of adults and children living at a given address.
     * @param address a String representing the address.
     * @return a ChildAlertDTO object containing the lists of FullNameAndAgeDTO for child and adults.
     */
    public ChildAlertDTO createChildAlertList(String address) {
        logger.debug("Creating a child alert list at this address: {}", address);
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
            logger.info("Created a child alert with {} child and {} adults living at this address: {}", childrenList.size(), adultsList.size(), address);
            return new ChildAlertDTO(childrenList, adultsList);
    }

    /**
     * Created a list of emails for the persons living at the given city.
     * @param city a String representing a city.
     * @return a list of String containing the emails of each person living in the given city.
     */
    public List<String> getPersonsEmails(String city) {
        logger.debug("Retrieving all persons emails for the city: {}", city);
        ArrayList<String> emails = new ArrayList<>();
        for (Person person : personRepository.findPersonsByCity(city)) {
            emails.add(person.getEmail());
        }
        logger.info("Created a list of {} emails for the city: {}", emails.size(), city);
        return emails;
    }

    /**
     * Create a new Person.
     * @param person the Person object to create.
     * @return the created Person object.
     */
    public Person createPerson(Person person) {
        logger.debug("Creating a person: {} {}", person.getFirstName(), person.getLastName());
        return personRepository.save(person);
    }

    /**
     * Deletes a Person matching the given inputs.
     * @param firstName a String representing the first name of the person to delete.
     * @param lastName a String representing the last name of the person to delete.
     */
    public void deleteOnePerson(String firstName, String lastName) {
        logger.debug("Deleting {} {}", firstName, lastName);
        personRepository.delete(firstName, lastName);
    }

    /**
     * Updates an existing person.
     * @param person a Person object to update.
     * @return the updated Person.
     */
    public Person updatePerson(Person person) {
        logger.debug("Updating person: {} {}", person.getFirstName(), person.getLastName());
        return personRepository.update(person);
    }
}