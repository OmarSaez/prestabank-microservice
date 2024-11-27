package com.example.m2_register.Controller;

import com.example.m2_register.Entity.UserEntity;
import com.example.m2_register.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class); //MEnsaje por cosnla


    @Autowired
    UserService userService;
    //Rescatar todos los usuarios
    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> listUsers(){
        List<UserEntity> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }
    //Rescatar un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id){
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    //Rescatar un usuario por su correo
    @GetMapping("/with/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email){
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    //Guardar un usuario
    @PostMapping("/")
    public ResponseEntity<?> saveUser(@RequestBody UserEntity user) {
        UserEntity newUser = userService.register(user);
        if (newUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo o rut ya está registrado.");
        }
        return ResponseEntity.ok(newUser);
    }
    //Actualizar un usuario
    @PutMapping("/")
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user){
        UserEntity updateUser = userService.updateUser(user);
        return ResponseEntity.ok(updateUser);
    }
    //Borrar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUserById(@PathVariable Long id) throws Exception{
        var isDeleted = userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
