package com.example.m6_totalcost.Controller;

import com.example.m6_totalcost.Service.TotalCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/totalcost")
public class TotalCostController {

    @Autowired
    TotalCostService totalCostService;

    // Calcular el costo del seguro basado en el monto del préstamo
    @GetMapping("/insurance")
    public ResponseEntity<Double> getInsurance(@RequestParam double loanAmount) {
        double insuranceCost = totalCostService.insurance(loanAmount);
        return ResponseEntity.ok(insuranceCost);
    }

    // Calcular la comisión basada en el monto del préstamo
    @GetMapping("/commission")
    public ResponseEntity<Double> getCommission(@RequestParam double loanAmount) {
        double commissionCost = totalCostService.commission(loanAmount);
        return ResponseEntity.ok(commissionCost);
    }

    // Calcular el costo total incluyendo préstamo, seguro, comisión y otros costos fijos
    @GetMapping("/total")
    public ResponseEntity<Double> getTotalCost(
            @RequestParam double loanAmount,
            @RequestParam double insurance,
            @RequestParam double commission) {
        double totalCost = totalCostService.totalCost(loanAmount, insurance, commission);
        return ResponseEntity.ok(totalCost);
    }
}
