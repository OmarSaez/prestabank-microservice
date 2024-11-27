package com.example.m4_evaluation.Controller;


import com.example.m4_evaluation.Service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    @Autowired
    EvaluationService evaluationService;

    //Requisito 1
    @GetMapping("/r1")
    public ResponseEntity<Integer> getR1paymentToIncome(@RequestParam int income, @RequestParam double monthlyPayment) {
        return ResponseEntity.ok(evaluationService.paymentToIncome(income, monthlyPayment));
    }

    //Requisito 3
    @GetMapping("/r3")
    public ResponseEntity<Integer> getR3seiority(@RequestParam int veteran) {
        return ResponseEntity.ok(evaluationService.seniority(veteran));
    }

    //Requisito 4
    @GetMapping("/r4")
    public ResponseEntity<Integer> getR4DebtToIncome(@RequestParam int income, @RequestParam int totalDebt, @RequestParam double monthlyPayment) {
        return ResponseEntity.ok(evaluationService.debtToIncome(income, totalDebt, monthlyPayment));
    }

    // Requisito 6: Verificar la edad del solicitante
    @GetMapping("/r6")
    public ResponseEntity<Integer> getR6YearOld(@RequestParam long idUser) {
        return ResponseEntity.ok(evaluationService.yearOld(idUser));
    }

    // Requisito 7: Evaluar la capacidad de ahorro (todos los parámetros como @RequestParam)
    @GetMapping("/r7")
    public ResponseEntity<List<Integer>> getR7SavingSkills(
            @RequestParam int income,
            @RequestParam double loanAmount,
            @RequestParam int accountYears,
            @RequestParam ArrayList<Integer> balanceLast12,
            @RequestParam ArrayList<Integer> bankDeposit,
            @RequestParam ArrayList<Integer> withdrawals) {

        List<Integer> result = evaluationService.savingSkills(income, loanAmount, accountYears, balanceLast12, bankDeposit, withdrawals);
        return ResponseEntity.ok(result);
    }

}
