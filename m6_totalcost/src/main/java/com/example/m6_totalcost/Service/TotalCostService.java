package com.example.m6_totalcost.Service;

import org.springframework.stereotype.Service;

@Service
public class TotalCostService {

    public double insurance(double loanAmount){
        return loanAmount*0.0003;
    }

    public double commission(double loanAmount){
        return loanAmount*0.01;
    }

    public double totalCost(double loanAmount, double insurance, double commission){
        return loanAmount+insurance+commission+20000;
    }
}
