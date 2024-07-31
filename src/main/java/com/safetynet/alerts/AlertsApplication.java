package com.safetynet.alerts;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Map;

import com.safetynet.alerts.repository.PersonRepository;

@SpringBootApplication
public class AlertsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		PersonRepository personRepository = new PersonRepository();
		Map<String, Object> data = personRepository.getData();
		System.out.println(personRepository.getPersons(data));
	}

}
