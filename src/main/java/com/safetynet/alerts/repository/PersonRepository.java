package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.IOException;
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

}
