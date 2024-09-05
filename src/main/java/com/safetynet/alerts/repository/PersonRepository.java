package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonRepository {
    List<Person> persons;
    private final DataRepository dataRepository;

    public PersonRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListPersons();
    }

    public void createListPersons() {
        try {
            JsonNode data = dataRepository.getData();
            JsonNode personsNode = data.get("persons");
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Person>> typeReferenceList = new TypeReference<List<Person>>() {};
            this.persons = objectMapper.readValue(personsNode.traverse(), typeReferenceList);
        } catch (IOException e) {
            throw new RuntimeException("Error while creating Persons List", e);
        }
    }

    public List<Person> findAll() {
        return persons;
    }

    public Person findPersonByFullName(String firstName, String lastName) {
        for (Person person : persons) {
            if(person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                return person;
            }
        }
        return null;
    }

    public List<Person> findPersonsByLastName(String lastName) {
        List<Person> outputPersonsList = new ArrayList<>();
        for (Person person : persons) {
            if(person.getLastName().equals(lastName)) {
                outputPersonsList.add(person);
            }
        }
        return outputPersonsList;
    }

    public List<Person> findPersonByAddress(String address) {
        List<Person> outputPersonsList = new ArrayList<>();
        for (Person person : persons) {
            if(person.getAddress().equals(address)) {
                outputPersonsList.add(person);
            }
        }
        return outputPersonsList;
    }

    public List<Person> findPersonsByCity(String city) {
        List<Person> outputPersonsList = new ArrayList<>();
        for (Person person : persons) {
            if(person.getCity().equals(city)) {
                outputPersonsList.add(person);
            }
        }
        return outputPersonsList;
    }

    public Person save(Person person) throws IOException {
        persons.add(person);
        updatePersonsList(persons);
        return person;
    }

    public void delete(Person inputPerson) throws IOException {
        for (Person person : persons) {
            if(person.equals(inputPerson)) {
                persons.remove(person);
                updatePersonsList(persons);
                return;
            }
        }
        throw new IllegalArgumentException("Person not found: " + inputPerson);
    }

    public Person update(Person inputPerson) throws IOException {
        for (Person person : persons) {
            if(person.equals(inputPerson)) {
                persons.set(persons.indexOf(person), inputPerson);
                updatePersonsList(persons);
                return person;
            }
        }
        throw new IllegalArgumentException("Person not found: " + inputPerson);
    }

    public void updatePersonsList(List<Person> persons) throws IOException {
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        ((ObjectNode) dataRepository.getData()).set("persons", objectMapper.valueToTree(persons));
        dataRepository.writeData(rootNode);
    }
}
