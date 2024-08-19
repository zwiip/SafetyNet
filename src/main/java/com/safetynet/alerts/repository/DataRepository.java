package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
public class DataRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getData() {
        try {
            File file = new File("./src/main/resources/data.json");
            JsonNode data = objectMapper.readTree(file);
            return data;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
