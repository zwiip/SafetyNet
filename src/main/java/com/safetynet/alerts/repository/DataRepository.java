package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DataRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public void getData() {
        try {
            File file = new File("./src/main/resources/data.json");
            JsonNode jsonNode = objectMapper.readTree(file);
            System.out.println(createListPersons(jsonNode));
        //    createFireStations(jsonNode);
        //    createMedicalRecords(jsonNode);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Person> createListPersons(JsonNode jsonNode) throws IOException {
        JsonNode personsNode = jsonNode.get("persons");
        TypeReference<List<Person>> typeReferenceList = new TypeReference<List<Person>>() {};

        /*List<Person> listPersons = new ArrayList<>();

        for (JsonNode node : personsNode) {
            Person person = new Person(node.get("firstName").asText(), node.get("lastName").asText(), node.get("address").asText(), node.get("city").asText(), node.get("zip").asText(), node.get("phone").asText(), node.get("email").asText());
            listPersons.add(person);
        }
        return listPersons;
        */
        return new ObjectMapper().readValue(personsNode.traverse(), typeReferenceList);
    }
}
