package com.safetynet.alerts.dao;

import com.safetynet.alerts.model.Person;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;


/**
 * Classe qui communique avec la base de données pour les opérations CRUD.
 */
public class PersonDaoImpl implements PersonDao {
    public static List<Person> persons = new ArrayList<>();

    @Override
    public List<Person> findAll() {
        return null;
    }

    public void createPersonList(Map<String, List<Map<String, String>>> data) {
        List<Map<String, String>> inputDataPersons = data.get("persons");
        for(Map<String, String> person : inputDataPersons) {
            persons.add(new Person(person.get("firstName"), person.get("lastName"), person.get("address"), person.get("city"), person.get("zip"), person.get("phone"), person.get("email")));
        }
    }
}
