package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

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

    @PostMapping(value="/person")
    public ResponseEntity<Person> addOnePerson(@RequestBody Person person) throws IOException {
        Person personAdded = personService.createPerson(person);
        if (Objects.isNull(personAdded)) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstname}/{lastname}")
                .buildAndExpand(personAdded.getFirstName(), personAdded.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value="/person")
    public void deleteOnePerson(@RequestParam String firstName, @RequestParam String lastName) throws IOException {
        personService.deleteOnePerson(firstName, lastName);
    }

    @PutMapping(value="/person")
    public ResponseEntity<Person> updateOnePerson(@RequestBody Person person) throws IOException {
        Person personToUpdate = personService.updatePerson(person);
        if (Objects.isNull(personToUpdate)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(personToUpdate);
    }
}