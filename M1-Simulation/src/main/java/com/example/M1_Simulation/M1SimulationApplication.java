package com.example.M1_Simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class M1SimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(M1SimulationApplication.class, args);
	}

}
