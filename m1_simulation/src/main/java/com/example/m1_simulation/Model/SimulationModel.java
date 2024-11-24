package com.example.m1_simulation.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationModel {
    private double loanAmount;
    private double yearInterestRate;
    private int yearPayments;
}
