package com.example.M2_Register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class M2RegisterApplication {

	public static void main(String[] args) {
		SpringApplication.run(M2RegisterApplication.class, args);
	}

}
