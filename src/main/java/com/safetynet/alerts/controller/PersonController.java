package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/persons")
    public List<Person> getPersons() {
        return personService.getPersons();
    }

    @GetMapping("/person/{firstname}/{lastname}")
    public Person getOnePerson(@PathVariable("firstname") String firstName, @PathVariable("lastname") String lastName) {
        return personService.getOnePerson(firstName, lastName);
    }

    @GetMapping("/childAlert")
    public ChildAlertDTO getChildAlertList(@RequestParam String address) {
        return personService.createChildAlertList(address);
    }

    @GetMapping("/personInfolastName")
        public List<PersonInfoLastNameDTO> getPersonInfoLastName(@RequestParam String lastName) {
        return personService.getPersonsByLastName(lastName);
    }

    @GetMapping("/communityEmail")
    public List<String> getCommunityEmail(@RequestParam String city) {
        return personService.getPersonsEmails(city);
    }
}