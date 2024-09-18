package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class PersonRepository {
    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(PersonRepository.class);
    List<Person> persons;
    private final DataRepository dataRepository;

    /* CONSTRUCTOR */
    public PersonRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListPersons();
    }

    /* METHODS */

    /**
     * Creates a list of Person objects from the JSON file. It fetches the "persons" key in the JSON data,
     *  parses it into a list of Person objects, validates the data by removing duplicates, and updates the list.
     *
     * @throws RuntimeException if an error occurs while creating the list
     */
    public void createListPersons() {
        try {
            logger.debug("Creating persons list from the JSON file.");
            JsonNode data = dataRepository.getData();
            JsonNode personsNode = data.get("persons");

            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Person>> typeReferenceList = new TypeReference<>() {};
            List<Person> personsData = objectMapper.readValue(personsNode.traverse(), typeReferenceList);

            this.persons = validatePersonsData(personsData);
            updatePersonsList(this.persons);
            logger.debug("Persons list created, with {} persons.", persons.size());
        } catch (IOException e) {
            throw new RuntimeException("Error while creating Persons List", e);
        }
    }

    /**
     * Validates the list of Persons by removing any duplicate entries.
     * A duplicate is identified when two persons have the same first and last name.
     * If duplicates are found, they are removed.
     *
     * @param persons the list of Person objects to validate.
     * @return a new list of Person objects with duplicates removed.
     */
    public List<Person> validatePersonsData (List<Person> persons) {
        Set<String> uniquePersons = new HashSet<>();
        List<Person> filteredPersons = new ArrayList<>();

        for (Person person : persons) {
            String fullName = person.getFirstName() + " " + person.getLastName();
            if (!uniquePersons.contains(fullName)) {
                uniquePersons.add(fullName);
                filteredPersons.add(person);
            } else {
                logger.warn("Duplicate person found and removed: {}", fullName);
            }
        }
        logger.debug("Validation complete. Total persons after removing duplicates: {}", filteredPersons.size());
        return filteredPersons;
    }

    /**
     * Retrieves the list of all persons
     *
     * @return the list of persons
     */
    public List<Person> findAll() {
        logger.debug("Finding all persons.");
        return persons;
    }

    /**
     * Browse the persons list to find someone matching a first name and a last name
     *
     * @param firstName a string representing the first name of the person we are looking for
     * @param lastName a string representing the last name of the person we are looking for
     * @return the matching person or null if nobody is found
     */
    public Person findPersonByFullName(String firstName, String lastName) {
        logger.debug("Finding person named {} {}.", firstName, lastName);
        for (Person person : persons) {
            if(person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                logger.debug("Found person named {} {}.", firstName, lastName);
                return person;
            }
        }
        logger.warn("Person not found: {} {}.", firstName, lastName);
        return null;
    }

    /**
     * Browse the persons list to find every person with the given last name
     *
     * @param lastName a string representing the last name of the persons we are looking for
     * @return a list of persons with the given last name
     */
    public List<Person> findPersonsByLastName(String lastName) {
        logger.debug("Finding persons named {}.", lastName);
        List<Person> outputPersonsList = new ArrayList<>();
        for (Person person : persons) {
            if(person.getLastName().equals(lastName)) {
                outputPersonsList.add(person);
                logger.debug("Adding {} {} to the list of persons named {}.", person.getFirstName(), person.getLastName(), lastName);
            }
        }
        logger.debug("Found {} persons named {}.", outputPersonsList.size(), lastName);
        return outputPersonsList;
    }

    /**
     * Browse the persons list to find those living at the given address.
     *
     * @param address a String representing an address
     * @return the list of the persons living at the address
     */
    public List<Person> findPersonByAddress(String address) {
        logger.debug("Finding persons living at {}.", address);
        List<Person> outputPersonsList = new ArrayList<>();
        for (Person person : persons) {
            if(person.getAddress().equals(address)) {
                outputPersonsList.add(person);
                logger.debug("Adding {} {} to the list of persons living at {}.", person.getFirstName(), person.getLastName(), address);
            }
        }
        logger.debug("Found {} persons living at {}.", outputPersonsList.size(), address);
        return outputPersonsList;
    }

    /**
     * Browse the persons list to find those living at the given city.
     *
     * @param city a String representing a city
     * @return the list of the persons living in the city
     */
    public List<Person> findPersonsByCity(String city) {
        logger.debug("Finding persons living in {}.", city);
        List<Person> outputPersonsList = new ArrayList<>();
        for (Person person : persons) {
            if(person.getCity().equals(city)) {
                outputPersonsList.add(person);
                logger.debug("Adding {} {} to the list of persons in {}.", person.getFirstName(), person.getLastName(), city);
            }
        }
        logger.debug("Found {} persons living in {}.", outputPersonsList.size(), city);
        return outputPersonsList;
    }

    /**
     * Add a new person to the list of persons and update the JSON file.
     *
     * @param person a new Person to add.
     * @return the added person
     */
    public Person save(Person person) {
        logger.debug("Saving person {} {}.", person.getFirstName(), person.getLastName());
        persons.add(person);
        updatePersonsList(persons);
        logger.info("Person saved: {} {}", person.getFirstName(), person.getLastName());
        return person;
    }

    /**
     * Delete a person matching the first name and the last name and update the JSON file.
     *
     * @param firstName a String representing the first name of the person we want to delete.
     * @param lastName a String representing the last name of the person we want to delete.
     * @throws IllegalArgumentException if no person matching the inputs is found.
     */
    public void delete(String firstName, String lastName) throws ResourceNotFoundException {
        logger.debug("Deleting person named {} {}.", firstName, lastName);
        for (Person person : persons) {
            if(person.getFirstName().equals(firstName) &&
               person.getLastName().equals(lastName)) {
                persons.remove(person);
                updatePersonsList(persons);
                logger.info("{} {} deleted.", person.getFirstName(), person.getLastName());
                return;
            }
        }
        throw new ResourceNotFoundException("Person not found: " + firstName + " " + lastName);
    }

    /**
     * Update an existing person with the new data and update the JSON file.
     *
     * @param inputPerson a person with updated data.
     * @return the updated person
     * @throws  IllegalArgumentException if no person is found with the input.
     */
    public Person update(Person inputPerson) {
        logger.debug("Updating person {} {}.", inputPerson.getFirstName(), inputPerson.getLastName());
        for (Person person : persons) {
            if(person.getFirstName().equals(inputPerson.getFirstName()) &&
                    person.getLastName().equals(inputPerson.getLastName())) {
                persons.set(persons.indexOf(person), inputPerson);
                updatePersonsList(persons);
                logger.info("Person {} {} updated.", person.getFirstName(), person.getLastName());
                return inputPerson;
            }
        }
        throw new ResourceNotFoundException("Person not found: " + inputPerson.getFirstName() + " " + inputPerson.getLastName());
    }

    /**
     * Turn the persons list into a JsonNode Object in order to write it as values for the key "persons" in the JSON file.
     *
     * @param persons a list of person with new data to write to the JSON file.
     */
    public void updatePersonsList(List<Person> persons) {
        logger.debug("Updating persons list.");
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        rootNode.set("persons", objectMapper.valueToTree(persons));
        dataRepository.writeData(rootNode);
        logger.info("Persons list updated, now {} persons.", persons.size());
    }
}
