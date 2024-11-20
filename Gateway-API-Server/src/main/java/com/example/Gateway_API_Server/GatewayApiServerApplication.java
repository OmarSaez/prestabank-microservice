package com.example.Gateway_API_Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GatewayApiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApiServerApplication.class, args);
	}

}
