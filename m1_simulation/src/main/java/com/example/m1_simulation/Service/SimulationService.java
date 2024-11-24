package com.example.m1_simulation.Service;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SimulationService {
    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class); //Mensajes por consola

    public double simulateLoan(double loanAmount, double yearInterestRate, int yearPayments) {

        logger.info("--Se entro al simulador de creditos bien");

        double monthlyInterestRate = yearInterestRate/12/100;
        int totalPayments = yearPayments*12;

        // Cálculo de la cuota mensual basado en la fórmula de amortización
        double upPart = Math.pow(1 + monthlyInterestRate, totalPayments) * monthlyInterestRate;
        double downPart = Math.pow(1 + monthlyInterestRate, totalPayments) - 1;
        double monthlyPayment = (upPart / downPart) * loanAmount;

        logger.info("--Valor final calculado: {}", monthlyPayment);
        return monthlyPayment;
    }
}
