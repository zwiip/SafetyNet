package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Repository
public class PersonRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getData() {
        try {
            Map<String, Object> dataMap = objectMapper.readValue(new File("./src/main/resources/data.json"), new TypeReference<Map<String, Object>>(){});
            return dataMap;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object getPersons(Map<String, Object> data) {
        Object persons = data.get("persons");
        return persons;
    }
}
