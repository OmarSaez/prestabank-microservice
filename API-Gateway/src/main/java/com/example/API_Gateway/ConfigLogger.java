package com.example.API_Gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ConfigLogger {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLogger.class);

    @Value("${eureka.client.service-url.default-zone}")
    private String eurekaServerUrl;

    @Value("${eureka.client.register-with-eureka}")
    private boolean registerWithEureka;

    @Value("${eureka.client.fetch-registry}")
    private boolean fetchRegistry;

    @PostConstruct
    public void logConfig() {
        logger.info("Eureka Configuración:");
        logger.info(" - Service URL: {}", eurekaServerUrl);
        logger.info(" - Register with Eureka: {}", registerWithEureka);
        logger.info(" - Fetch registry: {}", fetchRegistry);
    }
}

