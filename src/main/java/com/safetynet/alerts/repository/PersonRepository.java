package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonRepository {
    List<Person> persons;

    public void createListPersons(JsonNode jsonNode) throws IOException {
        JsonNode personsNode = jsonNode.get("persons");
        TypeReference<List<Person>> typeReferenceList = new TypeReference<List<Person>>() {};
        List<Person> persons = new ObjectMapper().readValue(personsNode.traverse(), typeReferenceList);
        this.persons = persons;
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

    public Person save(Person person) {
        persons.add(person);
        return person;
    }

    public void delete(Person person) {
        persons.remove(person);
    }

    public Person update(Person person) {
        persons.set(persons.indexOf(person), person);
        return person;
    }
}
