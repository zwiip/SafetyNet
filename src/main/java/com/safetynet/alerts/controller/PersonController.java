package com.safetynet.alerts.controller;

import com.safetynet.alerts.dao.PersonDao;
import com.safetynet.alerts.model.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController {
    private final PersonDao personDao;

    public PersonController(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GetMapping("/persons")
    public List<Person> getPersons() {
        return personDao.findAll();
    }
}
