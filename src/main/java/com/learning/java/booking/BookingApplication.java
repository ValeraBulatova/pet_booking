package com.learning.java.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

//	open http://localhost:8080/valera to see the result
	// TODO: 07.09.2021 fix it

	@GetMapping("/valera")
	public String hello(@RequestParam(value = "name", defaultValue = "Valera") String name) {
		return String.format("Hello %s!", name);
	}

}
