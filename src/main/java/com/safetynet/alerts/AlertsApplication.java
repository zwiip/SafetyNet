package com.safetynet.alerts;

import com.fasterxml.jackson.databind.JsonNode;
import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.safetynet.alerts.repository.DataRepository;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class AlertsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private PersonRepository personRepository;

	@Autowired
	PersonController personController;

	@Autowired
	FireStationRepository fireStationRepository;

	@Override
	public void run(String... args) throws Exception {
		JsonNode data = dataRepository.getData();
		personRepository.createListPersons(data);
		fireStationRepository.createListFireStations(data);
	}
}
