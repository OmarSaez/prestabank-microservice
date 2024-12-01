import * as React from 'react';
import { useState } from "react";
import { useParams } from 'react-router-dom';
import NavBar from './NavBar';
import { useNavigate } from 'react-router-dom';
import loanService from '../services/loan.service';
import Button from '@mui/material/Button'; 
import { Box, MenuItem, Select, InputLabel, TextField, FormControl, Grid, FormControlLabel, Radio, RadioGroup, FormLabel } from '@mui/material';

const ApplyForLoan = () => {

    const navigate = useNavigate();
    const { id } = useParams();

    const [loanType, setLoanType] = useState('');
    const [propertyValue, setPropertyValue] = useState('');
    const [requiredLoan, setRequiredLoan] = useState('');
    const [yearsToPay, setYearsToPay] = useState('');
    const [yearInterestRate, setYearInterestRate] = useState('');
    const [income, setIncome] = useState('');
    const [veteran, setVeteran] = useState('');
    const [totaldebt, setTotaldebt] = useState('');
    const [papers, setPapers] = useState(null); // Ahora maneja un archivo
    const [isIndependent, setIsIndependent] = useState('');

    const loanTypeLimits = {
        "1": { maxLoanPercentage: 80, maxYears: 30, interestRate: 4.5 },
        "2": { maxLoanPercentage: 70, maxYears: 20, interestRate: 5.5 },
        "3": { maxLoanPercentage: 60, maxYears: 25, interestRate: 6.5 },
        "4": { maxLoanPercentage: 50, maxYears: 15, interestRate: 5.0 },
    };

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

    // Manejador para el archivo (para actualizar el estado)
    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        if (selectedFile && selectedFile.type === 'application/pdf') {
            setPapers(selectedFile);
        } else {
            alert("Por favor, selecciona un archivo PDF.");
            setPapers(null);
        }
    };
    
    //Se crea la solicitud a mandar
    const handleLoanCreate = async () => {
        const loan = {
            idUser: id,
            type: loanType,
            yearInterest: yearInterestRate,
            maxDuration: yearsToPay,
            income,
            veteran,
            totaldebt,
            loanAmount: requiredLoan,
            isIndependent: isIndependent === 'yes' ? 1 : 0,
        };
        
        console.log("----DATOS QUE SE MANDARAN A LOAN.SERVICE.JS----");
        console.log("LOAN que se ingreso por el FRONT", loan);// Log oara verificar el loan a madnar
        console.log("Archivo seleccionado en el FRONT:", papers); // Verificación de que el archivo se seleccionó
        
        
        try {
            await loanService.create(loan, papers);
            alert("Se mandó la solicitud de crédito");
            navigate(`/home/${id}`);
        } catch (error) {
            console.error("Error al intentar enviar el préstamo:", error);
            alert("Ocurrió un error al intentar mandar el préstamo. Por favor, intenta nuevamente.");
        }
    };
    
    
    
    return (
        <div>
            <NavBar id={id} />
            <h1>Solicitar un crédito</h1>
            <h2>Por favor ingrese todos los datos y al final comprobantes de ellos</h2>
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
                            type="number" 
                            value={income}  
                            onChange={(e) => setIncome(e.target.value)} 
                            label="Salario mensual" 
                            placeholder="Ingreso mensual" 
                        />
                        <br/>

                        <TextField 
                            type="number" 
                            value={veteran}  
                            onChange={(e) => setVeteran(e.target.value)} 
                            label="Antiguedad en el trabajo" 
                            placeholder="tiempo en años que lleva dentro del trabajo" 
                        />
                        <br/>

                        <TextField 
                            type="number" 
                            value={totaldebt}  
                            onChange={(e) => setTotaldebt(e.target.value)} 
                            label="Total de deudas mensuales" 
                            placeholder="Ingrese el total de deudas que tenga actualmente cada mes" 
                        />
                        <br/>
                        <FormControl component="fieldset">
                            <FormLabel component="legend">¿Eres trabajador independiente?</FormLabel>
                            <RadioGroup row value={isIndependent} onChange={e => setIsIndependent(e.target.value)}>
                                <FormControlLabel value="yes" control={<Radio />} label="Sí" />
                                <FormControlLabel value="no" control={<Radio />} label="No" />
                            </RadioGroup>
                        </FormControl>

                        <input type="file" accept="application/pdf" onChange={handleFileChange} />
                        <Button variant="contained" onClick={handleLoanCreate} sx={{ mt: 2 }}>Enviar solicitud de crédito</Button>
                    </FormControl>
                </Grid>
            </Box>
        </div>
    );
};

export default ApplyForLoan;
