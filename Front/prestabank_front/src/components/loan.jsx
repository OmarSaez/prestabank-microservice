import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import loanService from "../services/loan.service";
import userService from "../services/user.service"; 
import { useNavigate } from 'react-router-dom'; 
import Button from '@mui/material/Button'; 
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';

const Loan = () => {
    const navigate = useNavigate();
    const { id, idLoan } = useParams();
    const [loanData, setLoanData] = useState(null);
    const [userData, setUserData] = useState(null);
    const [open, setOpen] = useState(false);
    const [selectedStatus, setSelectedStatus] = useState('');
    const [selectedRequirement, setSelectedRequirement] = useState('');
    const [openRequestR2, setOpenRequestR2] = useState(false); //false para abrir/cerrar correctamente
    const [accountYears, setAccountYears] = useState(""); // Años de la cuenta de ahorro
    const [balanceLast12, setBalanceLast12] = useState(Array(12).fill("")); // Balance de los últimos 12 meses



    useEffect(() => {
        loanService
          .get(idLoan)
          .then((response) => {
            setLoanData(response.data);
          })
          .catch((error) => {
            console.error("Error al obtener los datos del préstamo:", error);
          });
    }, [idLoan]);

    useEffect(() => {
        userService
          .get(id)
          .then((response) => {
            setUserData(response.data);
          })
          .catch((error) => {
            console.error("Error al obtener los datos del usuario:", error);
          });
    }, [id]);

    if (!loanData || !userData) {
        return <div>Cargando...</div>;
    }

    const getStatusText = (status) => {
        switch (status) {
          case 1: return "Revisión inicial";
          case 2: return "Pendiente de documentación";
          case 3: return "En evaluación";
          case 4: return "Pre-aprobado";
          case 5: return "Aprobación final";
          case 6: return "Aprobada";
          case 7: return "Rechazada";
          case 8: return "Cancelada por el cliente";
          case 9: return "En desembolso";
          default: return "Estado desconocido";
        }
    };

    const getColor = (score) => {
        if (score == 8 || score == 7) return '#FF0000'; // rojo para rechazado o cancelada
        if (score == 6) return '#00FF00'; // verde si se aprobo
        if (score == 4 || score == 5) return '#FFC933'; //amarillo si esta previo a ser aprobada
        return '#4169E1'; // azul para todo lo demás
      };

    const getTypeText = (type) => {
        switch (type) {
            case 1: return "Primera vivienda";
            case 2: return "Segunda vivienda";
            case 3: return "Comercial";
            case 4: return "Remodelacion";
            default: return "Tipo de Crédito desconocido";
        }
    };

    const getRequirementStatus = (status) => {
        switch (status) {
        case 0: return "Rechazado";
        case 1: return "Cumplido";
        case 2: return "Pendiente";
        case 3: return "Requiere otra revisión";
        default: return "Estado desconocido";
        }
    };

    const handleOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleChangeStatus = () => {
        const updatedLoanData = { ...loanData, status: Number(selectedStatus) };
        
        loanService.update(updatedLoanData)
            .then(response => {
                console.log("Estado actualizado en el servidor:", response.data);
                setLoanData(updatedLoanData);
            })
            .catch(error => {
                console.error("Error al actualizar el estado:", error);
            });

        handleClose();
    };

    const handleOpenRequestR2 = () => {
        setOpenRequestR2(true); // Actualizado para abrir el diálogo
    }

    const handleCloseRequestR2 = () => {
        setOpenRequestR2(false); // Actualizado para cerrar el diálogo
    }

    const handleChangeRequestR2 = () => {
        const updatedLoanData = { ...loanData };
        updatedLoanData.evalue[1] = Number(selectedRequirement);
        
        loanService.update(updatedLoanData)
            .then(response => {
                console.log("Estado actualizado en el servidor:", response.data);
                setLoanData(updatedLoanData);
            })
            .catch(error => {
                console.error("Error al actualizar el estado:", error);
            });

        handleCloseRequestR2();
    };

    // Función para manejar cambios en el input de años
    const handleAccountYearsChange = (e) => {
        setAccountYears(Number(e.target.value));
    };

    // Función para manejar cambios en cada input del balance
    const handleBalanceChange = (index, value) => {
        const newBalance = [...balanceLast12];
        // Permitir que el valor sea "" para poder borrar el input
        newBalance[index] = value === "" ? "" : Number(value);
        setBalanceLast12(newBalance);
    };
    

    const handleSubmit = () => {
        const updatedLoanData = { ...loanData };
    
        // Verifica que cada campo de balance no esté vacío
        if (balanceLast12.some(balance => balance === "")) {
            alert("Por favor, complete todos los campos de balance antes de continuar.");
            return; // Salir de la función si hay campos vacíos
        }
    
        console.log('Balance Last 12:', balanceLast12);
    
        loanService.updateExecutive(updatedLoanData, accountYears, balanceLast12)
            .then(response => {
                console.log("Estado actualizado en el servidor:", response.data);
                setLoanData(updatedLoanData);
            })
            .catch(error => {
                console.log('Balance Last 12:', balanceLast12);
                console.error("Error al actualizar el estado:", error);
            });
    };


    return (
        <div>
            <h2>Detalles del Crédito</h2>
            <p>ID Préstamo: {idLoan}</p>
            <p>ID Usuario: {id}</p>

            {/* Datos user */}
            <h4>Datos del cliente:</h4>
            <p>RUT: {userData.rut} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Nombre y Apellido: {userData.name} {userData.lastName}</p>
            <p>
                Ingreso: {new Intl.NumberFormat("es-CL", { style: "decimal", minimumFractionDigits: 0, maximumFractionDigits: 2 }).format(loanData.income)}
                &nbsp;&nbsp;&nbsp;
                Antigüedad en trabajo: {loanData.veteran}
                &nbsp;&nbsp;&nbsp;
                Deudas: {new Intl.NumberFormat("es-CL", { style: "decimal", minimumFractionDigits: 0, maximumFractionDigits: 2 }).format(loanData.totaldebt)}
            </p>
            <p>Fecha de nacimiento: {userData.birthDay}/{userData.birthMonth}/{userData.birthYear}</p>

            {/* Datos solicud */}
            <h4>Datos de la solicitud:</h4>
            <p style={{ color: getColor(loanData.status), fontWeight: 'bold'  }}>Estado: {getStatusText(loanData.status)}</p>
            <p>Tipo de Crédito: {getTypeText(loanData.type)}</p>
            <p>Valor del prestamo: {new Intl.NumberFormat("es-CL", { style: "decimal", minimumFractionDigits: 0, maximumFractionDigits: 2 }).format(loanData.loanAmount)}</p>
            <p>Interés Anual: {loanData.yearInterest}&nbsp;&nbsp; 
            Cuota Mensual:{" "}
            {new Intl.NumberFormat("es-CL", {
                style: "decimal",
                minimumFractionDigits: 0,
                maximumFractionDigits: 4,
            }).format(loanData.monthlyPayment)}&nbsp;&nbsp; 
            Total de Meses: {loanData.totalPayments}</p>
            
            <br/>
            <br/>

            {/* Sección de requisitos */}
            <h2>Estados de los Requisitos:</h2>

            <h4>Requisito 1: Relación Cuota/Ingreso</h4>
            <p>La relacion cuota ingreso no debe ser mayor al 35%</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[0])}</p>

            <h4>Requisito 2: Historial Crediticio del Cliente</h4>
            <p>Se debe revisar el historial crediticio del cliente en DICOM, esta es una revision manual que debe hacer un ejecutivo</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[1])}</p>

            <Button 
                variant="contained" 
                onClick={handleOpenRequestR2}
                type="button"
                sx={{ mt: 2 }}
            >
                Cambiar estado
            </Button>

            <Dialog open={openRequestR2} onClose={handleCloseRequestR2}> {/* Corregido el prop 'open' */}
                <DialogTitle>Cambiar estado de la solicitud</DialogTitle>
                <DialogContent>
                    <Select
                        value={selectedRequirement}
                        onChange={(e) => setSelectedRequirement(e.target.value)}
                        fullWidth
                    >
                        <MenuItem value={0}>Rechazado</MenuItem>
                        <MenuItem value={1}>Cumplido</MenuItem>
                        <MenuItem value={2}>Pendiente de documentación</MenuItem>
                        <MenuItem value={3}>Requiere otra revision</MenuItem>
                    </Select>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseRequestR2}>Cancelar</Button>
                    <Button onClick={handleChangeRequestR2}>Guardar</Button>
                </DialogActions>
            </Dialog>

            <h4>Requisito 3: Antigüedad Laboral y Estabilidad</h4>
            <p>Se verifica que el solicitante tenga almenos 2 años en us trabajo actual, no tomar en cuenta el estado si es un independiente ya que no aplica</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[2])}</p>

            {/* Mensaje de advertencia en texto rojo si el solicitante es independiente */}
            {loanData.isIndependent === 1 && (
                <p style={{ color: 'red', fontWeight: 'bold' }}>
                    Advertencia: El solicitante es independiente. Se debe revisar sus ingresos de los últimos 2 o más años para evaluar su estabilidad financiera.
                </p>
            )}


            <h4>Requisito 4: Relación Deuda/Ingreso </h4>
            <p>La relacion entre la deuda incluyendo la proyeccion de la cuota mensual y el ingreso del solicitante no debe ser mayor al 50%</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[3])}</p>

            <h4>Requisito 5: Monto Máximo de Financiamiento</h4>
            <p>Estas limitante son controladas al momento que se realiza la solicitud</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[4])}</p>

            <h4>Requisito 6: Edad del Solicitante </h4>
            <p>El solicitante no puede tener 70 años o más</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[5])}</p>

            <h4>Requisito 7: Capacidad de Ahorro</h4>
            <p>Para calcular los diversos factores y asi evaluar la capacidad de ahorro del solcitante porfavor ingrese los años de la cuenta de ahorro y el saldo de los ultimos 12 meses</p>
            <p>Estado: {getRequirementStatus(loanData.evalue[6])}</p>

            {/* Campo de entrada para los años de la cuenta de ahorro */}
            <label>Años de la cuenta de ahorro: </label>
            <input
                type="number"
                value={accountYears}
                onChange={handleAccountYearsChange}
                min="0"
            />
            <br/>
            <br/>
            {/* Campos de entrada para el balance de los últimos 12 meses */}
            <label>Balance de los últimos 12 meses:</label>
            <div>
                {balanceLast12.map((balance, index) => (
                    <div key={index}>
                        <label>Mes {index + 1}: </label>
                        <input
                            type="number"
                            value={balance === "" ? "" : balance} 
                            onChange={(e) => handleBalanceChange(index, e.target.value)}
                            min="0"
                        />
                    </div>
                ))}
            </div>

            <br/>
            {/* Botón para enviar la actualización */}
            <button onClick={handleSubmit}>Actualizar Requisito de Ahorro</button>
       
            <br/>
            <br/>
            <Button 
                variant="contained" 
                onClick={() => navigate(`/home/${id}`)}
                type="button"
                sx={{ mt: 2, mr: 2}}
            >
                Volver
            </Button>

            <Button 
                variant="contained" 
                onClick={handleOpen} // Abre el diálogo
                type="button"
                sx={{ mt: 2 }}
            >
                Cambiar estado de la solicitud
            </Button>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Cambiar estado de la solicitud</DialogTitle>
                <DialogContent>
                    <Select
                        value={selectedStatus}
                        onChange={(e) => setSelectedStatus(e.target.value)}
                        fullWidth
                    >
                        <MenuItem value={1}>Revisión inicial</MenuItem>
                        <MenuItem value={2}>Pendiente de documentación</MenuItem>
                        <MenuItem value={3}>En evaluación</MenuItem>
                        <MenuItem value={4}>Pre-aprobado</MenuItem>
                        <MenuItem value={5}>Aprobación final</MenuItem>
                        <MenuItem value={6}>Aprobada</MenuItem>
                        <MenuItem value={7}>Rechazada</MenuItem>
                        <MenuItem value={8}>Cancelada por el cliente</MenuItem>
                        <MenuItem value={9}>En desembolso</MenuItem>
                    </Select>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>Cancelar</Button>
                    <Button onClick={handleChangeStatus}>Guardar</Button>
                </DialogActions>
            </Dialog>

        </div>
    );
};

export default Loan;
