package com.example.m1_simulation.Controller;


import com.example.m1_simulation.Model.SimulationModel;
import com.example.m1_simulation.Service.SimulationService;
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