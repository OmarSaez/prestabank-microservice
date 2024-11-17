package com.example.M1_Simulation.Controller;

import com.example.M1_Simulation.Model.SimulationModel;
import com.example.M1_Simulation.Service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan/simulate")
public class SimulationController {

    @Autowired
    SimulationService simulationService;

    @PostMapping("/")
    public ResponseEntity<Double> simulation(@RequestBody SimulationModel request){
        double monthlyPayment = simulationService.simulateLoan(
                request.getLoanAmount(),
                request.getYearInterestRate(),
                request.getYearPayments()
        );
        return ResponseEntity.ok(monthlyPayment);
    }
}
