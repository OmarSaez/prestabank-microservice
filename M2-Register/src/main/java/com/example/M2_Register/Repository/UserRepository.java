package com.example.M2_Register.Repository;

import com.example.M2_Register.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByRut(String rut);
    public UserEntity findUserByEmail(String email);

}

