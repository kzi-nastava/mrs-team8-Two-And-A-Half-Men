package com.project.backend.controllers;

import com.project.backend.DTO.RegistretionDTO;
import com.project.backend.DTO.UserLoginDTO;
import com.project.backend.DTO.UserLoginRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDTO credentials) {

        if (credentials.getUsername() == null || credentials.getPassword() == null) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Username and password are required"));
        }
        if ("user".equals(credentials.getUsername()) && "pass".equals(credentials.getPassword())) {
            //Setting dommy user for test purpose
            UserLoginDTO user = new UserLoginDTO();
            user.setId(1L);
            return ResponseEntity.ok(Map.of("user",user,"message", "Login successful", "token", "dummy--token"));
        }
        return ResponseEntity.status(401)
                .body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistretionDTO userData) {
        if(userData.getUsername().equals("peraperic@gmail.com")) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "User already exists"));
        }
        return ResponseEntity.status(201)
                .body(Map.of("message", "User registered successfully"));

    }

    @PostMapping("/drivers/register")
    public ResponseEntity<?> registerDriver(@RequestBody Map<String, Object> driverData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, Object> emailData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> passwordData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
}