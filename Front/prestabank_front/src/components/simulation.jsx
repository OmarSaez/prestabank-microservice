import * as React from 'react';
import { useState, useEffect } from "react";
import { useParams } from 'react-router-dom';
import NavBar from './NavBar';
import Button from '@mui/material/Button'; 
import { useNavigate } from 'react-router-dom';
import { Box, Typography, MenuItem, Select, InputLabel, TextField, FormControl, Grid } from '@mui/material';
import loanService from '../services/loan.service';

const Simulation = () => {

    const navigate = useNavigate();
    const { id } = useParams();

    // Estados para los inputs y la cuota mensual calculada
    const [loanType, setLoanType] = useState('');
    const [propertyValue, setPropertyValue] = useState('');
    const [requiredLoan, setRequiredLoan] = useState('');
    const [yearsToPay, setYearsToPay] = useState('');
    const [yearInterestRate, setYearInterestRate] = useState('');
    const [monthlyPayment, setMonthlyPayment] = useState(''); // Estado para la cuota mensual calculada

    // Restricciones basadas en el tipo de préstamo
    const loanTypeLimits = {
        "1": { maxLoanPercentage: 80, maxYears: 30, interestRate: 4.5 },
        "2": { maxLoanPercentage: 70, maxYears: 20, interestRate: 5.5 },
        "3": { maxLoanPercentage: 60, maxYears: 25, interestRate: 6.5 },
        "4": { maxLoanPercentage: 50, maxYears: 15, interestRate: 5.0 },
    };

    // Manejar cambio del tipo de préstamo
    const handleLoanTypeChange = (e) => {
        const selectedType = e.target.value;
        setLoanType(selectedType);
        setRequiredLoan('');
        setYearsToPay('');
        setYearInterestRate(loanTypeLimits[selectedType].interestRate);
    };

    const handlePropertyValueChange = (e) => setPropertyValue(e.target.value);

    const handleRequiredLoanChange = (e) => {
        const maxLoan = (propertyValue * loanTypeLimits[loanType].maxLoanPercentage) / 100;
        if (e.target.value <= maxLoan) {
            setRequiredLoan(e.target.value);
        } else {
            alert(`ADVERTENCIA: El préstamo máximo permitido es el ${loanTypeLimits[loanType].maxLoanPercentage}% del valor del inmueble (monto máximo: ${maxLoan})`);
        }
    };

    const handleYearsToPayChange = (e) => {
        const maxYears = loanTypeLimits[loanType].maxYears;
        if (e.target.value <= maxYears && e.target.value >= 0) {
            setYearsToPay(e.target.value);
        } else {
            alert(`ADVERTENCIA: El máximo de años permitidos es ${maxYears}`);
        }
    };

    const handleCalculate = async () => {
        const simulate = {
            loanAmount: requiredLoan,
            yearInterestRate: yearInterestRate,
            yearPayments: yearsToPay,
        };

        try {
            const response = await loanService.simulateLoan(simulate); // SimulateLoan devuelve la cuota mensual calculada
            // Formateamos el número para mostrar con separadores de miles y sin decimales
            const formattedPayment = new Intl.NumberFormat('es-CL').format(Math.round(response.data));
            setMonthlyPayment(formattedPayment); // Guarda el valor de la cuota mensual en el estado
                
        } catch (error) {
            console.error("Error al intentar calcular el préstamo:", error);
            alert("Ocurrió un error al intentar calcular el préstamo. Por favor, intenta nuevamente.");
        }
    };

    return (
        <div>
            <NavBar id={id} />
            <h1>Simulación de crédito</h1>
            <h2>Aquí puedes ver cuánto saldrá cada cuota de la hipoteca</h2>
            <Box sx={{ '& > :not(style)': { ml: 0, mr: -30, mt: 6, mb: 5 } }}>
                <Grid container spacing={1}>
                    <FormControl fullWidth margin="normal">
                        
                        <InputLabel>Tipo de préstamo</InputLabel>
                        <Select value={loanType} onChange={handleLoanTypeChange} label="Tipo de préstamo">
                            <MenuItem value="" disabled>Selecciona el tipo de préstamo</MenuItem>
                            <MenuItem value="1">Primera vivienda</MenuItem>
                            <MenuItem value="2">Segunda vivienda</MenuItem>
                            <MenuItem value="3">Propiedad comercial</MenuItem>
                            <MenuItem value="4">Remodelación</MenuItem>
                        </Select>
                        <br/>

                        <TextField 
                            type="number" 
                            value={propertyValue} 
                            onChange={handlePropertyValueChange} 
                            label="Valor del inmueble" 
                            placeholder="Ingresa el valor del inmueble" 
                        />
                        <br/>

                        <TextField 
                            type="number" 
                            value={requiredLoan} 
                            onChange={handleRequiredLoanChange} 
                            label="Préstamo requerido" 
                            placeholder="Ingresa el préstamo requerido" 
                            disabled={!loanType || !propertyValue} 
                        />
                        <br/>

                        <TextField 
                            type="number" 
                            value={yearsToPay} 
                            onChange={handleYearsToPayChange} 
                            label="Total de años a pagar" 
                            placeholder="Ingresa los años para pagar" 
                            disabled={!loanType} 
                        />
                        <br/>

                        <TextField 
                            type="text" 
                            value={monthlyPayment} 
                            label="Cuota mensual calculada" 
                            color="secondary" 
                            focused 
                            InputProps={{ readOnly: true }} 
                        />
                        <br/>

                        <Button 
                            variant="contained" 
                            onClick={handleCalculate}
                            type="button"
                            sx={{ mt: 2 }}
                        >
                            Calcular credito Chile
                        </Button>

                    </FormControl>

                    <Button 
                        variant="contained" 
                        onClick={() => navigate(`/home/${id}`)}
                        type="button"
                        sx={{ mt: 2 }}
                    >
                        Volver
                    </Button>
                </Grid>
            </Box>
            <h4>Dependiendo del tipo de préstamo cambiará el monto máximo que puedas solicitar</h4>
        </div>
    );
};

export default Simulation;
