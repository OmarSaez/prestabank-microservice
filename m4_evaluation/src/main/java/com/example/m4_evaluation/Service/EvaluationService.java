package com.example.m4_evaluation.Service;

import com.example.m4_evaluation.Model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

@Service
public class EvaluationService {

    @Autowired
    RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EvaluationService.class); //MEnsaje por cosnla

    //Calcular edad todo bien
    public static int calcularYearOld(int diaNacimiento, int mesNacimiento, int anioNacimiento) {
        // Crear un objeto LocalDate con la fecha de nacimiento
        LocalDate fechaNacimiento = LocalDate.of(anioNacimiento, mesNacimiento, diaNacimiento);

        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Calcular la edad usando Period
        Period periodo = Period.between(fechaNacimiento, fechaActual);

        // Devolver la cantidad de años
        return periodo.getYears();
    }

    //R1 relacion cuotas ingresos, si es mayor 35% se rechaza todo bien
    public int paymentToIncome(int income, double monthlyPayment) {
        double ratio = (monthlyPayment / income) * 100;

        if (ratio > 35) {
            return 0;
        } else {
            return 1;
        }
    }

    //R2 historial, no es posible evaluar se pone 2 de pendiente

    //R3 se ve la antiguedad laboral todo bien
    public int seniority(int veteran) {
        if (veteran < 2) {
            return 0;
        } else {
            return 1;
        }
    }

    //R4 ver la relacion deuda ingreso todo bien
    public int debtToIncome(int income, int totalDebt, double monthlyPayment) {
        double debt = totalDebt+monthlyPayment;
        double ratio = (debt / income) * 100;
        logger.info("Deuda total {} + {} = {}", totalDebt, monthlyPayment, debt);
        logger.info("Ratio: {} / {} = {}", debt, income, ratio);

        if (ratio > 50) {
            return 0;
        } else {
            return 1;
        }
    }

    //R5 es una condicional de tabla y es ase limita en otro lado el monto a pedir maximo

    //R6 verificar la edad del solicitante todo bien
    public int yearOld(long idUser) {

        UserModel user = restTemplate.getForObject("http://REGISTER/api/user/" + idUser, UserModel.class);
        int edad = calcularYearOld(user.getBirthDay(), user.getBirthMonth(), user.getBirthYear());

        if (edad > 69) {
            return 0;
        } else {
            return 1;
        }
    }

    //R7 Verificar la capacidad de ahorro, se utiliza una tabla de puntaje

    //(Credito, Frecuencia de depositos, Años de antiguedad de la cuenta, list(Saldo ultimos 12 meses), list(Retiro de los ultimos 12 meses)
    public ArrayList<Integer> savingSkills(int income, double loanAmount, int acountYears, ArrayList<Integer> balanceLast12, ArrayList<Integer> bankDeposit, ArrayList<Integer> withdrawals) {
        ArrayList<Integer> listEvalue = new ArrayList<>();
        //agregamos R71
        listEvalue.add(minBalance(balanceLast12.get(0), loanAmount));
        //Agregamos R72
        listEvalue.add(recordSaving(balanceLast12, withdrawals));
        //Agregamos R73
        listEvalue.add(periodicDeposits(bankDeposit, income));
        //Agremos R74
        listEvalue.add(ratioBalanceVeteran(acountYears, balanceLast12, loanAmount));
        //Agregamos R75
        listEvalue.add(recentWithdrawals(withdrawals, balanceLast12));

        return listEvalue;
    }

    //R71 saldo minimo del 10% del prestamo solicitado, aca loan es el prestamo en numero, no la entidad
    public int minBalance(int balance, double loan) {
        logger.info("--Requisito de ahorro1");
        double tenPercentage = loan * 0.1;
        if (balance >= tenPercentage) {
            return 1;
        } else {
            return 0;
        }
    }

    //R72 Saldo positivo los ultimos 12 meses y no retiros del mas del 50%
    public int recordSaving(ArrayList<Integer> balanceLast12, ArrayList<Integer> last12Withdrawals) {
        logger.info("--Requisito de ahorro2");
        //Ciclo que analiza los saldos de los 12 meses
        for (int i = 0; i < balanceLast12.size(); i++) {
            int value = balanceLast12.get(i);
            if (value <= 0) {
                return 0;
            }
        }

        for (int i = 1; i < last12Withdrawals.size(); i++) {

            int value = last12Withdrawals.get(i);
            int balance = balanceLast12.get(i-1);
            if (value > balance * 0.5) {
                return 0;
            }
        }

        return 1;//Si ya paso ambos ciclos significa que esta todo bien
    }

    //R73 verificar si los depositos son periodicos y la suma total suman almenos un 5% del ingreso mensual
    public int periodicDeposits(ArrayList<Integer> bankDeposit12, int income) {
        logger.info("--Requisito de ahorro3");
        int frecuencyDeposits = 0;

        int acum = 0;
        //Acumulador que calcula el saldo total de los ultimos 12 meses
        for (int i = 0; i < bankDeposit12.size(); i++) {
            acum = bankDeposit12.get(i) + acum;
            if (bankDeposit12.get(i) > 0){
                frecuencyDeposits = frecuencyDeposits + 1;
            }
        }

        frecuencyDeposits = frecuencyDeposits/12;

        //Verifica la frecuencia de deposito
        if (frecuencyDeposits > 3) {
            logger.info("++ la frecuencia de depositos no es fuciente {}", frecuencyDeposits);
            return 0;
        }

        //verifica si el acumulado es mayor al 5% del ingreso mensual
        logger.info("--Acum de los depositos: {}, ingresos: {}", acum, income);
        if (acum < income * 0.05) {
            return 0;
        } else {
            return 1;
        }
    }


    //R74 Si la cuenta de ahorros tiene menos de 2 años el acum debe ser 20% del prestamo solicitado
    public int ratioBalanceVeteran(int acountYears, ArrayList<Integer> balanceLast12, double loan) {
        logger.info("--Requisito de ahorro4");
        //NOTA: ACA LOAN ES EL VALOR DEL PRESTAMO, NO ES LA ENTIDAD LOAN
        int acum = 0;
        //Acumulador que calcula el saldo total de los ultimos 12 meses
        for (int i = 0; i < balanceLast12.size(); i++) {
            acum = balanceLast12.get(i) + acum;
        }

        //Consulta cuantos años tiene la cuenta
        if (acountYears < 2) {
            if (acum < loan * 0.2) {
                return 0;
            } else {
                return 1;
            }

            //Si es mayor a 2 años, que el acum solo sea mayor al 10% del prestamo
        } else if (acum < loan * 0.1) {
            return 0;
        } else {
            return 1;
        }
    }

    //R75 verifica si los retiros de los ultimos 6 meses son mayores a 30% del saldo actual, PENDIENTE LIMITAR A SOLO 6 MESES, se puede hacer con el largo de la lista
    public int recentWithdrawals(ArrayList<Integer> last12Withdrawals, ArrayList<Integer> balance12){
        logger.info("--Requisito de ahorro5");
        int large = last12Withdrawals.size();
        large = large-5;
        for (int i = 0; i < large; i++) {
            int withdrawal = last12Withdrawals.get(i+1);
            int balance = balance12.get(i);
            if (withdrawal > balance * 0.3) {return 0;}
        }
        //si registro todos los meses y no retorno cero, entonces esta aprobado este punto
        return 1;
    }

}
