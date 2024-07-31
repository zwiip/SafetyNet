package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Repository
public class DataRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getData() {
        try {
            File file = new File("./src/main/resources/data.json");
            Map<String, Object> dataMap = objectMapper.readValue(file, new TypeReference<Map<String, Object>>(){});
            return dataMap;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
