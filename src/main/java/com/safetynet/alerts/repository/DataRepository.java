package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
public class DataRepository {
    public ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    public DataRepository() {
        this.file = new File("./src/main/resources/data.json");
    }

    public DataRepository(String path) {
        this.file = new File(path);
    }

    public JsonNode getData() {
        try {
            JsonNode data = objectMapper.readTree(file);
            return data;

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
