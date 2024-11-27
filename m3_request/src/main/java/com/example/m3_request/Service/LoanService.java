package com.example.m3_request.Service;

import com.example.m3_request.Entity.LoanEntity;
import com.example.m3_request.Repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class LoanService {

    @Autowired
    LoanRepository loanRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class); //MEnsaje por cosnla


    //rescatar todos los prestamos todo bien
    public ArrayList<LoanEntity> getLoans() {
        return (ArrayList<LoanEntity>) loanRepository.findAll();
    }

    //Rescata todos los loan por el ID de un user todo bien
    public ArrayList<LoanEntity> getLoansByIdUser(Long idUser) {
        return (ArrayList<LoanEntity>) loanRepository.findByIdUser(idUser);
    }

    //Guardar un prestamo todo bien
    public LoanEntity saveLoan(LoanEntity saveLoan) {

        saveLoan.setIngesurce(insurance(saveLoan.getLoanAmount()));
        saveLoan.setCommission(commission(saveLoan.getLoanAmount()));
        saveLoan.setTotalCost(totalCost(saveLoan.getLoanAmount(), saveLoan.getIngesurce(), saveLoan.getCommission()));

        saveLoan.setMonthlyInteresRate(saveLoan.getYearInterest()/12/100);
        saveLoan.setTotalPayments(saveLoan.getMaxDuration()*12);

        Double CalculeMonthlyPayment = Calculo(saveLoan);
        saveLoan.setMonthlyPayment(CalculeMonthlyPayment);

        int EvalueWithR1 = paymentToIncome(saveLoan);

        //R2 no se puede evaluar automaticamente

        int EvalueWithR3 = seniority(saveLoan);

        int EvalueWithR4 = debtToIncome(saveLoan);

        //R5 es tabla, se verifica en front, asi que siempre sera aprobado

        int EvalueWithR6 = yearOld(saveLoan);

        //La primera vez que se guarda, no se puede ingresar los datos necesarios para usar la funcion R7, son datos proporcionador por el evaluador (ejecutivo), se pone R7=2 de pendiente

        ArrayList <Integer> newEvalue = new ArrayList<>(Arrays.asList(EvalueWithR1, 2, EvalueWithR3, EvalueWithR4, 1, EvalueWithR6, 2));

        saveLoan.setEvalue(newEvalue);

        logger.info("--Lista de la evualuacion automatica: {}", newEvalue);

        return loanRepository.save(saveLoan);
    }

    //Buscar un prestamo por su ID todo bien
    public LoanEntity getLoanById(Long id) {
        return loanRepository.findById(id).get();
    }

    //Borrar un prestamo todo bien
    public boolean deleteLoan(Long id) throws Exception {
        try {
            if (!loanRepository.existsById(id)) {
                throw new Exception("Loan not found");
            }
            loanRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception("Error deleting loan: " + e.getMessage());
        }
    }


    //Modificar un prestamo todo bien
    public LoanEntity updateLoan(LoanEntity changeLoan) {

        changeLoan.setIngesurce(insurance(changeLoan.getLoanAmount()));
        changeLoan.setCommission(commission(changeLoan.getLoanAmount()));
        changeLoan.setTotalCost(totalCost(changeLoan.getLoanAmount(), changeLoan.getIngesurce(), changeLoan.getCommission()));


        changeLoan.setMonthlyInteresRate(changeLoan.getYearInterest()/12/100);
        changeLoan.setTotalPayments(changeLoan.getMaxDuration()*12);
        changeLoan.setMonthlyPayment(Calculo(changeLoan));

        int EvalueWithR1 = paymentToIncome(changeLoan);

        //R2 no se puede evaluar automaticamente

        int EvalueWithR3 = seniority(changeLoan);

        int EvalueWithR4 = debtToIncome(changeLoan);

        //R5 es tabla, no se verifica aca

        int EvalueWithR6 = yearOld(changeLoan);

        //La primera vez que se guarda, no se puede ingresar los datos necesarios para usar la funcion R7, son datos proporcionador por el evaluador (ejecutivo), se pone R7=2 de pendiente

        ArrayList <Integer> newEvalue = new ArrayList<>(Arrays.asList(EvalueWithR1, changeLoan.getEvalue().get(1), EvalueWithR3, EvalueWithR4, 1, EvalueWithR6, 2));
        changeLoan.setEvalue(newEvalue);

        return loanRepository.save(changeLoan);
    }

    //Modificar un prestamo usado al momento de evaluar, ya que requiere entradas proporcionadas por un ejecutivo evaluador todo bien
    public LoanEntity updateLoanWithExcutive(LoanEntity changeLoan, int acountYears, ArrayList<Integer> balanceLast12) {
        logger.info("--Modificador de ejecutivo");
        logger.info("--Balance12: {}", balanceLast12);

        changeLoan.setIngesurce(insurance(changeLoan.getLoanAmount()));
        changeLoan.setCommission(commission(changeLoan.getLoanAmount()));
        changeLoan.setTotalCost(totalCost(changeLoan.getLoanAmount(), changeLoan.getIngesurce(), changeLoan.getCommission()));

        ArrayList<Integer> bankDeposit = new ArrayList<>(); //se inicia la lista de despositos
        ArrayList<Integer> withdrawals = new ArrayList<>();// se inicia la lista de retiros

        bankDeposit.add(1);
        withdrawals.add(0);
        int evalue = 0;
        for (int i = 1; i < balanceLast12.size(); i++){
            evalue = balanceLast12.get(i)-balanceLast12.get(i-1); //mes actual menos el mes anterior
            if (evalue >= 0){//Se es mayor que cero, se deposito
                bankDeposit.add(evalue);
                withdrawals.add(0);
            }else{//Si es negativo entonces se retiro dinero
                bankDeposit.add(0);
                withdrawals.add(evalue*(-1));
            }

        }
        logger.info("--retiros: {}", withdrawals);
        logger.info("--depositos: {}", bankDeposit);

        //Re calculo de la cuota
        changeLoan.setMonthlyInteresRate(changeLoan.getYearInterest()/12/100);
        changeLoan.setTotalPayments(changeLoan.getMaxDuration()*12);
        changeLoan.setMonthlyPayment(Calculo(changeLoan));

        //Evaluacion del prestamo
        int EvalueWithR1 = paymentToIncome(changeLoan);

        //R2 no se puede evaluar automaticamente

        int EvalueWithR3 = seniority(changeLoan);

        int EvalueWithR4 = debtToIncome(changeLoan);

        //R5 es tabla, no se verifica aca

        int EvalueWithR6 = yearOld(changeLoan);

        ArrayList<Integer> saving = savingSkills(changeLoan, acountYears, balanceLast12, bankDeposit, withdrawals);
        changeLoan.setSaving(saving);
        logger.info("Lista de la evaluacion de ahorro: {}", saving);
        int acum = 0;
        //Acumulador de los puntos de ahorro
        for (int i = 0; i < saving.size(); i++) {
            acum = saving.get(i) + acum;
        }

        int EvalueWithR7 = 2;

        if (acum >= 5){EvalueWithR7 = 1;}
        if (acum == 4){EvalueWithR7 = 3;}
        if (acum == 3){EvalueWithR7 = 3;}
        if (acum < 3){EvalueWithR7 = 0;}

        ArrayList<Integer> newEvalue = new ArrayList<>(Arrays.asList(EvalueWithR1, changeLoan.getEvalue().get(1), EvalueWithR3, EvalueWithR4, 1, EvalueWithR6, EvalueWithR7));
        changeLoan.setEvalue(newEvalue);

        return loanRepository.save(changeLoan);
    }

    // Calculo de la cuota mensual todo bien
    public double Calculo(LoanEntity loan) {

        // Calcular la parte superior de la fórmula de amortización
        double upPart = 1 + loan.getMonthlyInteresRate(); // Tasa de interés mensual
        upPart = Math.pow(upPart, loan.getTotalPayments()); // (1 + tasa)^n
        upPart = upPart * loan.getMonthlyInteresRate(); // (1 + tasa)^n * tasa

        double downPart = 1 + loan.getMonthlyInteresRate(); // Tasa de interes mensual
        downPart = Math.pow(downPart, loan.getTotalPayments()); // (1 + tasa)^n
        downPart = downPart - 1; // (1 + tasa)^n - 1

        double result = upPart / downPart; // Division
        result = result * loan.getLoanAmount(); // Calculo * el monto del prestamo

        return result; // Devuelve el resultado
    }


    //Buscar todos los prestamos por el estado de la solicitud todo bien
    public ArrayList<LoanEntity> getLoanByStatus(int status) {
        return (ArrayList<LoanEntity>) loanRepository.findByStatus(status);
    }

    //Busca todos los prestamos por el tipo todo bien
    public ArrayList<LoanEntity> getLoanByType(int type) {
        return (ArrayList<LoanEntity>) loanRepository.findByType(type);
    }
