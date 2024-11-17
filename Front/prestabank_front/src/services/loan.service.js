import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/loan/');
}

const create = data => {
    return httpClient.post("/api/loan/", data);
}

const get = id =>  {
    return httpClient.get(`/api/loan/${id}`);
}

const getAllWithID = id => {
    return httpClient.get(`/api/loan/user/${id}`);
}

const update = data => {
    return httpClient.put('/api/loan/', data);
}

const remove = id => {
    return httpClient.delete(`/api/loan/${id}`);
}

const status = status => {
    return httpClient.get(`/api/loan/status/${status}`);
}

const type = type => {
    return httpClient.get(`/api/loan/type/${type}`);
}


const updateExecutive = (data, acountYears, balance) => {
    const balanceLast12 = balance.join(',');
    console.log('Balance Last 12 original:', balance);
    console.log('Balance Last 12:', balanceLast12); // Asegúrate de que esto sea un array
    console.log('Account Years:', acountYears); // Asegúrate de que esto sea un array

    return httpClient.put('/api/loan/executive', data, {params: { acountYears, balanceLast12 }});
};


const simulateLoan = (loanData) => {
    return httpClient.post("/api/loan/simulate/", loanData);
}


export default { getAll, create, get, getAllWithID, update, remove, status, type, updateExecutive, simulateLoan };
