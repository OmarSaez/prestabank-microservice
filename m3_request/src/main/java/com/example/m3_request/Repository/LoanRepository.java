package com.example.m3_request.Repository;

import com.example.m3_request.Entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    // Buscar los creditos de un usuario
    public List<LoanEntity> findByIdUser(Long idUser);
    // Buscar por apruebo o rechazo
    public List<LoanEntity> findByStatus(int status);
    // Buscar por el tipo de credito
    public List<LoanEntity> findByType(int type);
}

