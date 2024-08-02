package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
public class DataRepository {
    ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, List<Map<String, String>>> getData() {
        try {
            File file = new File("./src/main/resources/data.json");
            Map<String, List<Map<String, String>>> dataMap = objectMapper.readValue(file, new TypeReference<Map<String, List<Map<String, String>>>>(){});
            return dataMap;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
