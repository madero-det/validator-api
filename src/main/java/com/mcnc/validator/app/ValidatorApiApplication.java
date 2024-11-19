package com.mcnc.validator.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mcnc.validator.service")
@ComponentScan("com.mcnc.validator.controller")
public class ValidatorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValidatorApiApplication.class, args);
	}

}
