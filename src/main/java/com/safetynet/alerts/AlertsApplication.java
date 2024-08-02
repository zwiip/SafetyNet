package com.safetynet.alerts;

import com.safetynet.alerts.controller.PersonController;
import com.safetynet.alerts.dao.PersonDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;

import com.safetynet.alerts.repository.DataRepository;

@SpringBootApplication
public class AlertsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private PersonController personController;
	@Autowired
	PersonDaoImpl personDaoImpl;

	@Override
	public void run(String... args) throws Exception {
		Map<String, List<Map<String, String>>> data = dataRepository.getData();
		personDaoImpl.createPersonList(data);
		System.out.println(personController.getPersons());
	}
}
