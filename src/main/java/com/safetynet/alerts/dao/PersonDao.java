package com.safetynet.alerts.dao;

import com.safetynet.alerts.model.Person;

import java.util.List;

public interface PersonDao {
    List<Person> findAll();
}
