package com.safetynet.alerts;

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

	@Override
	public void run(String... args) throws Exception {
		dataRepository.getData();

	}
}
