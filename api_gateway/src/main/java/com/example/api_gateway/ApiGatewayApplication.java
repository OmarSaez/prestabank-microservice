package com.example.api_gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ApiGatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class); //MEnsaje por cosnla

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	@Order(1)  // Orden bajo para que se ejecute antes de otros filtros
	public GlobalFilter logRequestFilter() {
		return (exchange, chain) -> {
			var request = exchange.getRequest();
			logger.info("Request Method: {}", request.getMethod());
			logger.info("Request URI: {}", request.getURI());
			logger.info("Request Headers:");
			request.getHeaders().forEach((name, values) ->
					values.forEach(value -> logger.info("{}: {}", name, value))
			);
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				// Puedes agregar logging de respuesta aquí si lo necesitas
			}));
		};
	}



}
