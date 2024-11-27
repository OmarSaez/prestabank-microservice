package com.example.m4_evaluation.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String rut;
    private int birthDay;
    private int birthMonth;
    private int birthYear;
    private String password;
}
