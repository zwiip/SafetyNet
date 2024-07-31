package com.safetynet.alerts.service;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Map;

@Data
@Service
public class PersonService {

    public Object getPersons(Map<String, Object> data) {
        return data.get("persons");
    }

}
