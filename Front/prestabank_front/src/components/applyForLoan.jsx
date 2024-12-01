import * as React from 'react';
import { useState } from "react";
import { useParams, useNavigate } from 'react-router-dom';
import NavBar from './NavBar';
import loanService from '../services/loan.service';
import { Box, MenuItem, Select, InputLabel, TextField, FormControl, Grid, FormControlLabel, Radio, RadioGroup, FormLabel, Button } from '@mui/material';

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
    const [papers, setPapers] = useState(null);
    const [isIndependent, setIsIndependent] = useState('');
    const [fileName, setFileName] = useState(''); // Estado para almacenar el nombre del archivo



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

    const formatNumber = (value) => {
        const numericValue = value.replace(/\D/g, ''); // Elimina cualquier carácter que no sea un número
        return numericValue.replace(/\B(?=(\d{3})+(?!\d))/g, '.'); // Aplica el formato con puntos
    };
    
    const handlePropertyValueChange = (e) => {
        const formattedValue = formatNumber(e.target.value);
        setPropertyValue(formattedValue);
    };
    
    const handleRequiredLoanChange = (e) => {
        const rawValue = e.target.value.replace(/\D/g, ''); // Quitar puntos para comparar valores numéricos
        const maxLoan = (parseInt(propertyValue.replace(/\./g, '')) * loanTypeLimits[loanType].maxLoanPercentage) / 100;
        
        if (parseInt(rawValue) <= maxLoan) {
            const formattedValue = formatNumber(rawValue);
            setRequiredLoan(formattedValue);
        } else {
             alert(`ADVERTENCIA: El préstamo máximo permitido es el ${loanTypeLimits[loanType].maxLoanPercentage}% del valor del inmueble (monto máximo: ${formatNumber((maxLoan).toString())})`);
        }
    };
    
    const handleYearsToPayChange = (e) => {
        const maxYears = loanTypeLimits[loanType]?.maxYears || 0;
        const value = e.target.value;
        if (value <= maxYears && value >= 0) {
            setYearsToPay(value);
        } else {
            alert(`ADVERTENCIA: El máximo de años permitidos es ${maxYears}`);
        }
    };

    const handleIncomeChange = (e) => {
        const rawValue = e.target.value.replace(/\D/g, ''); // Elimina cualquier carácter no numérico
        const formattedValue = formatNumber(rawValue); // Aplica el formato con puntos
        setIncome(formattedValue); // Actualiza el estado con el valor formateado
    };

    const handleTotalDebtChange = (e) => {
        const rawValue = e.target.value.replace(/\D/g, ''); // Elimina caracteres no numéricos
        const formattedValue = formatNumber(rawValue); // Aplica el formato con puntos
        setTotaldebt(formattedValue); // Actualiza el estado con el valor formateado
    };
    
    
    
    

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        if (selectedFile && selectedFile.type === 'application/pdf') {
            setPapers(selectedFile);
            setFileName(selectedFile.name); // Actualiza el nombre del archivo
        } else {
            alert("Por favor, selecciona un archivo PDF.");
            setPapers(null);
            setFileName(''); // Limpiar el nombre si el archivo no es válido
        }
    };

    const handleLoanCreate = async () => {
        const loan = {
            idUser: id,
            type: loanType,
            yearInterest: yearInterestRate,
            maxDuration: yearsToPay,
            income: parseFloat(income.replace(/\./g, '')), // Convertir a número después de eliminar puntos
            veteran,
            totaldebt: parseFloat(totaldebt.replace(/\./g, '')), // Convertir a número después de eliminar puntos
            loanAmount: parseFloat(requiredLoan.replace(/\./g, '')), // Convertir a número después de eliminar puntos
            isIndependent: isIndependent === 'yes' ? 1 : 0,
        };
        

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
                            type="text" 
                            value={propertyValue} 
                            onChange={handlePropertyValueChange} 
                            label="Valor del inmueble" 
                            placeholder="Ingresa el valor del inmueble" 
                        />
                        <br/>

                        <TextField 
                            type="text" 
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
                            value={income}  
                            onChange={handleIncomeChange} 
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
                            type="text" 
                            value={totaldebt}  
                            onChange={handleTotalDebtChange} 
                            label="Total de deudas mensuales" 
                            placeholder="Ingrese el total de deudas que tenga actualmente cada mes" 
                        />

                        <FormControl component="fieldset">
                            <FormLabel component="legend">¿Eres trabajador independiente?</FormLabel>
                            <RadioGroup row value={isIndependent} onChange={e => setIsIndependent(e.target.value)}>
                                <FormControlLabel value="yes" control={<Radio />} label="Sí" />
                                <FormControlLabel value="no" control={<Radio />} label="No" />
                            </RadioGroup>
                        </FormControl>

                        <Button variant="outlined" component="label" sx={{ mt: 2 }}>
                            Cargar comprobante de documentación
                            <input type="file" hidden accept="application/pdf" onChange={handleFileChange} />
                        </Button>

                        {/* Mostrar el nombre del archivo cargado si existe */}
                        {fileName && (
                            <div style={{ marginTop: '10px' }}>
                                <strong>Archivo cargado: </strong> {fileName}
                            </div>
                        )}

                        <Button variant="contained" onClick={handleLoanCreate} sx={{ mt: 2 }}>
                            Enviar solicitud de crédito
                        </Button>
                    </FormControl>
                </Grid>
            </Box>
        </div>
    );
};

export default ApplyForLoan;
