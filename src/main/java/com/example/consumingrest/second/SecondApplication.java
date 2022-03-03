package com.example.consumingrest.second;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
public class SecondApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecondApplication.class, args);
	}

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger log = LoggerFactory.getLogger(SecondApplication.class);

	private static final String MAIN_SERVICE = "secondService";

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@GetMapping("/get")
	@ResponseStatus(HttpStatus.OK)
	@CircuitBreaker(name = MAIN_SERVICE , fallbackMethod="testFallBack")
	public ResponseEntity<String> getBatchesTest(){
		log.info("Calling main microservice from second microservice");
		String response = restTemplate.getForObject("http://localhost:8080/batches", String.class);
		return new ResponseEntity<String>(response, HttpStatus.OK);

	}

	private  ResponseEntity<String> testFallBack(Exception e){
		log.error("In fallback method due to {}", e.toString());
		return new ResponseEntity<String>("In fallback method as the main microservice is dowm", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
