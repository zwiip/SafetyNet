package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.*;
import com.safetynet.alerts.exceptions.EmptyResourceException;
import com.safetynet.alerts.exceptions.ResourceAlreadyExistException;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
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
     *
     * @return a list of Person objects.
     */
    public List<Person> getPersons() {
        logger.debug("Retrieving all persons");
        List<Person> persons = personRepository.findAll();
        logger.debug("Found {} persons", persons.size());
        return persons;
    }

    /**
     * Retrieve a person matching the inputs.
     *
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return a Person object.
     * @throws ResourceNotFoundException if nobody is matching the inputs.
     */
    public Person getOnePerson(String firstName, String lastName) throws ResourceNotFoundException {
        logger.debug("Retrieving {} {}", firstName, lastName);
        Person person = personRepository.findPersonByFullName(firstName, lastName);
        if (person == null) {
            throw new ResourceNotFoundException("No person named " + firstName + " " + lastName + " found.");
        }
        logger.debug("Found {} {}", person.getFirstName(), person.getLastName());
        return person;
    }

    /**
     * Creates a list of persons matching the given last name.
     *
     * @param lastName a String representing the last name of the persons we are looking for.
     * @return a List of PersonInfoLastNameDTO object containing the details and medical record of each person matching the name.
     * @throws ResourceNotFoundException if nobody is matching the name and the list is empty.
     */
    public List<PersonInfoLastNameDTO> getPersonsByLastName(String lastName) throws EmptyResourceException {
        logger.debug("Retrieving persons by last name: {}", lastName);
        List<PersonInfoLastNameDTO> personInfoLastNameDTOList = new ArrayList<>();

        List<Person> personsWithThisLastName = personRepository.findPersonsByLastName(lastName);
        if (personsWithThisLastName.isEmpty()) {
            throw new ResourceNotFoundException("No persons matching " + lastName + " found.");
        }

        for (Person person : personsWithThisLastName) {
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
     *
     * @param address a String representing the address.
     * @return a List of Person objects living at the given address.
     * @throws ResourceNotFoundException if the address is not found or nobody lives there.
     */
    public List<Person> getPersonsByAddress(String address) throws EmptyResourceException {
        logger.debug("Retrieving a list of persons by address: {}", address);
        List<Person> personsAtThisAddress = personRepository.findPersonByAddress(address);
        if (personsAtThisAddress.isEmpty()) {
            throw new ResourceNotFoundException("The address is not found or nobody is living there: " + address);
        }
        return personsAtThisAddress;
    }

    /**
     * Create an object containing the lists of adults and children living at a given address.*
     *
     * @param address a String representing the address.
     * @return a ChildAlertDTO object containing the lists of FullNameAndAgeDTO for child and adults.
     * @throws ResourceNotFoundException if the address is not found or nobody lives there.
     * @throws EmptyResourceException if no children lives at the given address.
     */
    public ChildAlertDTO createChildAlertList(String address) throws EmptyResourceException{
        logger.debug("Creating a child alert list at this address: {}", address);
        ArrayList<FullNameAndAgeDTO> adultsList = new ArrayList<>();
        ArrayList<FullNameAndAgeDTO> childrenList = new ArrayList<>();
        List<Person> personsAtThisAddress = getPersonsByAddress(address);

        if (personsAtThisAddress.isEmpty()) {
            throw new ResourceNotFoundException("Nobody is recorded at this address, please check your input: " + address);
        }

        for (Person person : personsAtThisAddress) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            if (age <= 18) {
                childrenList.add(new FullNameAndAgeDTO(person.getFirstName(), person.getLastName(), age));
            } else {
                adultsList.add(new FullNameAndAgeDTO(person.getFirstName(), person.getLastName(), age));
            }
        }
            if (childrenList.isEmpty()) {
                throw new EmptyResourceException("No child is living here");
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
        if (emails.isEmpty()) {
            throw new EmptyResourceException("No emails found, you may check the city: " + city);
        }
        logger.info("Created a list of {} emails for the city: {}", emails.size(), city);
        return emails;
    }

    /**
     * Create a List of persons living at the given address.
     *
     * @param address a String representing the address.
     * @return a list of Person objects.
     * @throws ResourceNotFoundException if the address is not found or nobody lives there.
     */
    public ArrayList<PersonAtThisAddressDTO> createPersonsAtThisAddressList(String address) {
        logger.debug("Creating persons list at the address {}", address);
        ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = new ArrayList<>();
        for ( Person person : getPersonsByAddress(address)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
            MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
            personsAtThisAddressList.add(new PersonAtThisAddressDTO(person.getLastName(), person.getPhone(), age, medicalRecordDTO));
        }
        if (personsAtThisAddressList.isEmpty()) {
            throw new ResourceNotFoundException("Address not found or nobody lives there: " + address);
        }
        logger.debug("Persons list created for address {}", address);
        return personsAtThisAddressList;
    }

    /**
     * Create a new Person.
     *
     * @param inputPerson the Person object to create.
     * @return the created Person object.
     * @throws ResourceAlreadyExistException if we find a Person with the same full name than the person to create.
     */
    public Person createPerson(Person inputPerson) throws ResourceAlreadyExistException {
        logger.debug("Creating a person: {} {}", inputPerson.getFirstName(), inputPerson.getLastName());
        String inputPersonFirstName = inputPerson.getFirstName();
        String inputPersonLastName = inputPerson.getLastName();
        for (Person person : getPersons()) {
            if (person.getFirstName().equals(inputPersonFirstName) && person.getLastName().equals(inputPersonLastName)) {
                throw new ResourceAlreadyExistException("Person: " + inputPersonFirstName + " " + inputPersonLastName + " already exists");
            }
        }
        return personRepository.save(inputPerson);
    }

    /**
     * Updates an existing person.
     *
     * @param inputPerson a Person object to update.
     * @return the updated Person.
     * @throws ResourceNotFoundException if the inputPerson is not found in the persons list.
     */
    public Person updatePerson(Person inputPerson) throws ResourceNotFoundException {
        logger.debug("Updating person: {} {}", inputPerson.getFirstName(), inputPerson.getLastName());
        Person personToUpdate = personRepository.findPersonByFullName(inputPerson.getFirstName(), inputPerson.getLastName());
        if (personToUpdate == null) {
            throw new ResourceNotFoundException("No person named " + inputPerson.getFirstName() + " " + inputPerson.getLastName() + " found.");
        }
        return personRepository.update(inputPerson);
    }

    /**
     * Deletes a Person matching the given inputs.
     *
     * @param firstName a String representing the first name of the person to delete.
     * @param lastName a String representing the last name of the person to delete.
     * @throws ResourceNotFoundException if the person is not found in the persons list.
     */
    public void deleteOnePerson(String firstName, String lastName) {
        logger.debug("Deleting {} {}", firstName, lastName);
        Person person = personRepository.findPersonByFullName(firstName, lastName);
        if (person == null) {
            throw new ResourceNotFoundException("No person named " + firstName + " " + lastName + " found.");
        }
        personRepository.delete(person);
    }

}