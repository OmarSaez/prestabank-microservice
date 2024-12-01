package com.example.m3_request.Service;

import com.example.m3_request.Config.RestTemplateConfig;
import com.example.m3_request.Entity.LoanEntity;
import com.example.m3_request.Repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


@Service
public class LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    RestTemplateConfig restTemplateConfig;

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
    public LoanEntity saveLoan(LoanEntity saveLoan, MultipartFile file) {

        try {
            if (file != null && !file.isEmpty()) {
                saveLoan.setPdfContent(file.getBytes());  // Guarda el contenido del PDF
            } else {
                saveLoan.setPdfContent(null);  // Deja espacio para agregar el PDF más tarde
            }
        } catch (IOException e) {
            saveLoan.setPdfContent(null);  // Establece null si ocurre un error
            logger.info("--Fallo al guardar el PDF");
            System.err.println("Error al procesar el archivo PDF: " + e.getMessage());
        }

        String urlInsurance = "http://TOTALCOST/api/totalcost/insurance?loanAmount=" + saveLoan.getLoanAmount();
        ResponseEntity<Double> response = restTemplateConfig.restTemplate().getForEntity(urlInsurance, Double.class);
        double insurance = response.getBody();
        saveLoan.setIngesurce(insurance);

        String urlCommission = "http://TOTALCOST/api/totalcost/commission?loanAmount=" + saveLoan.getLoanAmount();
        ResponseEntity<Double> response1 = restTemplateConfig.restTemplate().getForEntity(urlCommission, Double.class);
        double commission = response1.getBody();
        saveLoan.setCommission(commission);

        String urlTotalCost = "http://TOTALCOST/api/totalcost/total?loanAmount=" + saveLoan.getLoanAmount() + "&insurance=" + saveLoan.getIngesurce() + "&commission=" + saveLoan.getCommission();
        ResponseEntity<Double> response2 = restTemplateConfig.restTemplate().getForEntity(urlTotalCost, Double.class);
        saveLoan.setTotalCost(response2.getBody());

        saveLoan.setMonthlyInteresRate(saveLoan.getYearInterest() / 12 / 100);
        saveLoan.setTotalPayments(saveLoan.getMaxDuration() * 12);

        Double CalculeMonthlyPayment = Calculo(saveLoan);
        saveLoan.setMonthlyPayment(CalculeMonthlyPayment);

        //Se solicita evaluar R1
        String urlR1 = "http://EVALUATION/api/evaluation/r1?income=" + saveLoan.getIncome() + "&monthlyPayment=" + saveLoan.getMonthlyPayment();
        ResponseEntity<Integer> response3 = restTemplateConfig.restTemplate().getForEntity(urlR1, Integer.class);
        int EvalueWithR1 = response3.getBody();

        //R2 no se puede evaluar automaticamente

        //Se solicita evaluar R3
        String urlr3 = "http://EVALUATION/api/evaluation/r3?veteran=" + saveLoan.getVeteran();
        ResponseEntity<Integer> response4 = restTemplateConfig.restTemplate().getForEntity(urlr3, Integer.class);
        int EvalueWithR3 = response4.getBody();

        //Se solicita evaluar R4
        String urlr4 = "http://EVALUATION/api/evaluation/r4?income=" + saveLoan.getIncome() + "&totalDebt=" + saveLoan.getTotaldebt() + "&monthlyPayment=" + saveLoan.getMonthlyPayment();
        ResponseEntity<Integer> response5 = restTemplateConfig.restTemplate().getForEntity(urlr4, Integer.class);
        int EvalueWithR4 = response5.getBody();

        //R5 es tabla, se verifica en front, asi que siempre sera aprobado

        //Se solicita evaluar R6
        String urlr6 = "http://EVALUATION/api/evaluation/r6?idUser=" + saveLoan.getIdUser();
        ResponseEntity<Integer> response6 = restTemplateConfig.restTemplate().getForEntity(urlr6, Integer.class);
        int EvalueWithR6 = response6.getBody();

        //La primera vez que se guarda, no se puede ingresar los datos necesarios para usar la funcion R7, son datos proporcionador por el evaluador (ejecutivo), se pone R7=2 de pendiente

        ArrayList<Integer> newEvalue = new ArrayList<>(Arrays.asList(EvalueWithR1, 2, EvalueWithR3, EvalueWithR4, 1, EvalueWithR6, 2));

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

        String urlInsurance = "http://TOTALCOST/api/totalcost/insurance?loanAmount=" + changeLoan.getLoanAmount();
        ResponseEntity<Double> response = restTemplateConfig.restTemplate().getForEntity(urlInsurance, Double.class);
        double insurance = response.getBody();
        changeLoan.setIngesurce(insurance);

        String urlCommission = "http://TOTALCOST/api/totalcost/commission?loanAmount=" + changeLoan.getLoanAmount();
        ResponseEntity<Double> response1 = restTemplateConfig.restTemplate().getForEntity(urlCommission, Double.class);
        double commission = response1.getBody();
        changeLoan.setCommission(commission);

        String urlTotalCost = "http://TOTALCOST/api/totalcost/total?loanAmount=" + changeLoan.getLoanAmount() + "&insurance=" + changeLoan.getIngesurce() + "&commission=" + changeLoan.getCommission();
        ResponseEntity<Double> response2 = restTemplateConfig.restTemplate().getForEntity(urlTotalCost, Double.class);
        changeLoan.setTotalCost(response2.getBody());

        changeLoan.setMonthlyInteresRate(changeLoan.getYearInterest() / 12 / 100);
        changeLoan.setTotalPayments(changeLoan.getMaxDuration() * 12);

        Double CalculeMonthlyPayment = Calculo(changeLoan);
        changeLoan.setMonthlyPayment(CalculeMonthlyPayment);

        //Se solicita evaluar R1
        String urlR1 = "http://EVALUATION/api/evaluation/r1?income=" + changeLoan.getIncome() + "&monthlyPayment=" + changeLoan.getMonthlyPayment();
        ResponseEntity<Integer> response3 = restTemplateConfig.restTemplate().getForEntity(urlR1, Integer.class);
        int EvalueWithR1 = response3.getBody();

        //R2 no se puede evaluar automaticamente

        //Se solicita evaluar R3
        String urlr3 = "http://EVALUATION/api/evaluation/r3?veteran=" + changeLoan.getVeteran();
        ResponseEntity<Integer> response4 = restTemplateConfig.restTemplate().getForEntity(urlr3, Integer.class);
        int EvalueWithR3 = response4.getBody();

        //Se solicita evaluar R4
        String urlr4 = "http://EVALUATION/api/evaluation/r4?income=" + changeLoan.getIncome() + "&totalDebt=" + changeLoan.getTotaldebt() + "&monthlyPayment=" + changeLoan.getMonthlyPayment();
        ResponseEntity<Integer> response5 = restTemplateConfig.restTemplate().getForEntity(urlr4, Integer.class);
        int EvalueWithR4 = response5.getBody();

        //R5 es tabla, se verifica en front, asi que siempre sera aprobado

        //Se solicita evaluar R6
        String urlr6 = "http://EVALUATION/api/evaluation/r6?idUser=" + changeLoan.getIdUser();
        ResponseEntity<Integer> response6 = restTemplateConfig.restTemplate().getForEntity(urlr6, Integer.class);
        int EvalueWithR6 = response6.getBody();

        //La primera vez que se guarda, no se puede ingresar los datos necesarios para usar la funcion R7, son datos proporcionador por el evaluador (ejecutivo), se pone R7=2 de pendiente

        ArrayList<Integer> newEvalue = new ArrayList<>(Arrays.asList(EvalueWithR1, changeLoan.getEvalue().get(1), EvalueWithR3, EvalueWithR4, 1, EvalueWithR6, 2));
        changeLoan.setEvalue(newEvalue);

        return loanRepository.save(changeLoan);
    }

    //Modificar un prestamo usado al momento de evaluar, ya que requiere entradas proporcionadas por un ejecutivo evaluador todo bien
    public LoanEntity updateLoanWithExcutive(LoanEntity changeLoan, int acountYears, ArrayList<Integer> balanceLast12) {
        logger.info("--Modificador de ejecutivo");
        logger.info("--Balance12: {}", balanceLast12);

        //Se calcula los depositos realizados y los retiros
        ArrayList<Integer> bankDeposit = new ArrayList<>(); //se inicia la lista de despositos
        ArrayList<Integer> withdrawals = new ArrayList<>();// se inicia la lista de retiros

        bankDeposit.add(1);
        withdrawals.add(0);
        int evalue = 0;
        for (int i = 1; i < balanceLast12.size(); i++) {
            evalue = balanceLast12.get(i) - balanceLast12.get(i - 1); //mes actual menos el mes anterior
            if (evalue >= 0) {//Se es mayor que cero, se deposito
                bankDeposit.add(evalue);
                withdrawals.add(0);
            } else {//Si es negativo entonces se retiro dinero
                bankDeposit.add(0);
                withdrawals.add(evalue * (-1));
            }

        }
        logger.info("--retiros: {}", withdrawals);
        logger.info("--depositos: {}", bankDeposit);

        //Se calcula los costos del credito
        String urlInsurance = "http://TOTALCOST/api/totalcost/insurance?loanAmount=" + changeLoan.getLoanAmount();
        ResponseEntity<Double> response = restTemplateConfig.restTemplate().getForEntity(urlInsurance, Double.class);
        double insurance = response.getBody();
        changeLoan.setIngesurce(insurance);

        String urlCommission = "http://TOTALCOST/api/totalcost/commission?loanAmount=" + changeLoan.getLoanAmount();
        ResponseEntity<Double> response1 = restTemplateConfig.restTemplate().getForEntity(urlCommission, Double.class);
        double commission = response1.getBody();
        changeLoan.setCommission(commission);

        String urlTotalCost = "http://TOTALCOST/api/totalcost/total?loanAmount=" + changeLoan.getLoanAmount() + "&insurance=" + changeLoan.getIngesurce() + "&commission=" + changeLoan.getCommission();
        ResponseEntity<Double> response2 = restTemplateConfig.restTemplate().getForEntity(urlTotalCost, Double.class);
        changeLoan.setTotalCost(response2.getBody());

        changeLoan.setMonthlyInteresRate(changeLoan.getYearInterest() / 12 / 100);
        changeLoan.setTotalPayments(changeLoan.getMaxDuration() * 12);

        Double CalculeMonthlyPayment = Calculo(changeLoan);
        changeLoan.setMonthlyPayment(CalculeMonthlyPayment);

        //Se solicita evaluar R1
        String urlR1 = "http://EVALUATION/api/evaluation/r1?income=" + changeLoan.getIncome() + "&monthlyPayment=" + changeLoan.getMonthlyPayment();
        ResponseEntity<Integer> response3 = restTemplateConfig.restTemplate().getForEntity(urlR1, Integer.class);
        int EvalueWithR1 = response3.getBody();

        //R2 no se puede evaluar automaticamente

        //Se solicita evaluar R3
        String urlr3 = "http://EVALUATION/api/evaluation/r3?veteran=" + changeLoan.getVeteran();
        ResponseEntity<Integer> response4 = restTemplateConfig.restTemplate().getForEntity(urlr3, Integer.class);
        int EvalueWithR3 = response4.getBody();

        //Se solicita evaluar R4
        String urlr4 = "http://EVALUATION/api/evaluation/r4?income=" + changeLoan.getIncome() + "&totalDebt=" + changeLoan.getTotaldebt() + "&monthlyPayment=" + changeLoan.getMonthlyPayment();
        ResponseEntity<Integer> response5 = restTemplateConfig.restTemplate().getForEntity(urlr4, Integer.class);
        int EvalueWithR4 = response5.getBody();

        //R5 es tabla, se verifica en front, asi que siempre sera aprobado

        //Se solicita evaluar R6
        String urlr6 = "http://EVALUATION/api/evaluation/r6?idUser=" + changeLoan.getIdUser();
        ResponseEntity<Integer> response6 = restTemplateConfig.restTemplate().getForEntity(urlr6, Integer.class);
        int EvalueWithR6 = response6.getBody();

        //Se solicita evaluar R7
        String url7 = "http://EVALUATION/api/evaluation/r7?income=" + changeLoan.getIncome()
                + "&loanAmount=" + changeLoan.getLoanAmount()
                + "&accountYears=" + acountYears
                + "&balanceLast12=" + String.join(",", balanceLast12.stream().map(String::valueOf).toArray(String[]::new))
                + "&bankDeposit=" + String.join(",", bankDeposit.stream().map(String::valueOf).toArray(String[]::new))
                + "&withdrawals=" + String.join(",", withdrawals.stream().map(String::valueOf).toArray(String[]::new));
        ResponseEntity<ArrayList<Integer>> response7 = restTemplateConfig.restTemplate().exchange(
                url7, HttpMethod.GET, null, (Class<ArrayList<Integer>>) (Object) ArrayList.class);
        ArrayList<Integer> saving = response7.getBody();

        changeLoan.setSaving(saving);
        logger.info("Lista de la evaluacion de ahorro: {}", saving);
        int acum = 0;
        //Acumulador de los puntos de ahorro
        for (int i = 0; i < saving.size(); i++) {
            acum = saving.get(i) + acum;
        }

        int EvalueWithR7 = 2;

        if (acum >= 5) {
            EvalueWithR7 = 1;
        }
        if (acum == 4) {
            EvalueWithR7 = 3;
        }
        if (acum == 3) {
            EvalueWithR7 = 3;
        }
        if (acum < 3) {
            EvalueWithR7 = 0;
        }

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

}
