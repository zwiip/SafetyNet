package com.safetynet.alerts.controller;

import com.safetynet.alerts.controller.dto.ChildAlertDTO;
import com.safetynet.alerts.controller.dto.PersonInfoLastNameDTO;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class PersonController {

    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    private final PersonService personService;

    /* CONSTRUCTOR */
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /* METHODS */

    /**
     * This endpoint is used to fetch a list of all Persons.
     *
     * @return a list of Person objects.
     */
    @GetMapping("/persons")
    public List<Person> getPersons() {
        List<Person> persons = personService.getPersons();
        if (persons.isEmpty()) {
            logger.warn("Nobody found");
        } else {
            logger.info("Successful response, found {} persons", persons.size());
        }
        return persons;
    }

    /**
     * This endpoint returns the person matching with the first name and last name as Path Variable of the url.
     * Example usage:
     * GET /person/John/Boyd
     *
     * @param firstName a String representing the first name of the person we are looking for.
     * @param lastName a String representing the last name of the person we are looking for.
     * @return a Person object matching the inputs.
     */
    @GetMapping("/person/{first_name}/{last_name}")
    public Person getOnePerson(@PathVariable("first_name") String firstName, @PathVariable("last_name") String lastName) {
        Person person = personService.getOnePerson(firstName, lastName);
        if (person == null) {
            logger.warn("Nobody seems to match : {} {}", firstName, lastName);
        } else {
            logger.info("Successfully found {} {}", firstName, lastName);
        }
        return person;
    }

    /**
     * This endpoint returns a list of child and adults and their details at a given address.
     * Example usage:
     * GET /childAlert?address=221B Baker Street
     *
     * @param address a String representing.
     * @return a ChildAlertDTO object containing a list of child and a list of other members of the family living at the given address.
     */
    // TODO : demander pour l'utilité des if / else puisque les exceptions sont levées auparavant.
    @GetMapping("/childAlert")
    public ChildAlertDTO getChildAlertList(@RequestParam String address) {
        ChildAlertDTO childAlert = personService.createChildAlertList(address);
        if (childAlert == null) {
            logger.warn("Problem creating a child alert, please check the address: {}", address);
        }
        logger.info("Successfully created the child Alert for the address: {}", address);
        return childAlert;
    }

    /**
     * This endpoint returns a list of persons that have the given last name, and their details.
     * Example usage:
     * GET /personInfolastName?last_name=Doyle
     *
     * @param last_name a String representing the last name.
     * @return a list of PersonInfoLastNameDTO objects containing the list of the persons and their details.
     */
    @GetMapping("/personInfolastName")
        public List<PersonInfoLastNameDTO> getPersonInfoLastName(@RequestParam String last_name) {
        return personService.getPersonsByLastName(last_name);
    }

    /**
     * This endpoint returns a list of email for a given city.
     * Example usage:
     * GET /communityEmail?city=Paris
     *
     * @param city a String representing the city for which we make the request.
     * @return a list of String representing emails.
     */
    @GetMapping("/communityEmail")
    public List<String> getCommunityEmail(@RequestParam String city) {
        return personService.getPersonsEmails(city);
    }

    /**
     * This endpoint add a Person to the system.
     * It takes a json of a person in the body.
     * Example usage:
     * POST /person
     * Body: {"firstName": "Anne", "lastName": "Shirley", "address": "Green Gables", "city": "Avonlea", "zip": "12345", "phone": "0123456789", "email": "anne.shirley@avonlea.com"}
     *
     * @param person a json of a person in the body of the request.
     * @return a Response Entity with the HTTP status.
     *         - 201 CREATED: if the person has been successfully created,
     *         - 204 NO CONTENT: if the person could not be added.
     */
    @PostMapping(value="/person")
    public ResponseEntity<Person> addOnePerson(@RequestBody Person person) {
        Person personAdded = personService.createPerson(person);
        if (Objects.isNull(personAdded)) {
            logger.warn("Failed to add the person {}", person);
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstname}/{lastname}")
                .buildAndExpand(personAdded.getFirstName(), personAdded.getLastName())
                .toUri();
        logger.info("Added a new person: {} {}", personAdded.getFirstName(), personAdded.getLastName());
        return ResponseEntity.created(location).build();
    }

    /**
     * Update an existing person from the system with new details.
     * Example usage:
     * PUT /person
     * Body: {"firstName": "Anne", "lastName": "Shirley", "address": "Patty's House", "city": "Redmond", "zip": "54321", "phone": "0123456789", "email": "anne.shirley@redmond.com"}
     *
     * @param person a json of a person in the body of the request.
     * @return a response entity with the updated person and the HTTP status:
     *          - 200 OK: if the person has been successfully updated
     *          - 404 NOT FOUND: if the person hasn't been updated.
     */
    @PutMapping(value="/person")
    public ResponseEntity<Person> updateOnePerson(@RequestBody Person person) {
        Person personToUpdate = personService.updatePerson(person);
        if (Objects.isNull(personToUpdate)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(personToUpdate);
    }

    /**
     * This endpoint deletes a person from the system.
     * Example usage:
     * DELETE /person?first_name=Anne&last_name=Shirley
     *
     * @param first_name a String representing the person's first name, to add in the url
     * @param last_name a String representing the person's last name, to add in the url.
     * @return ResponseEntity<Void> indicating the HTTP status:
     *        - 200 OK: if the person has been successfully deleted,
     *        - 404 NOT FOUND: if the person hasn't been found.
     */
    @DeleteMapping(value="/person")
    public ResponseEntity<Void> deleteOnePerson(@RequestParam String first_name, @RequestParam String last_name) {
        if (!personService.deleteOnePerson(first_name, last_name)) {
            logger.warn("Failed to delete {} {}", first_name, last_name);
            return ResponseEntity.notFound().build();
        }
        logger.info("Successfully deleted {} {}", first_name, last_name);
        return ResponseEntity.ok().build();
    }
}