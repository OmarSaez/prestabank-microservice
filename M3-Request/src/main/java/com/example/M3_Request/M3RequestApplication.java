package com.example.M3_Request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class M3RequestApplication {

	public static void main(String[] args) {
		SpringApplication.run(M3RequestApplication.class, args);
	}

}