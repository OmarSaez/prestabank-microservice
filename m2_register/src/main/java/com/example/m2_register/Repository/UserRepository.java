package com.example.m2_register.Repository;


import com.example.m2_register.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByRut(String rut);
    public UserEntity findUserByEmail(String email);

}


