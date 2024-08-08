package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    public Person getOnePerson(String firstName, String lastName) {
        return personRepository.findPersonByFullName(firstName, lastName);
    }

}