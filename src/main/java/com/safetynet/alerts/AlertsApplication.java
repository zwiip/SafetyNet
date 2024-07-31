package com.safetynet.alerts;


import com.safetynet.alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
	private PersonService personService;

	@Override
	public void run(String... args) throws Exception {
		Map<String, Object> data = dataRepository.getData();
		System.out.println(personService.getPersons(data));
	}
}
