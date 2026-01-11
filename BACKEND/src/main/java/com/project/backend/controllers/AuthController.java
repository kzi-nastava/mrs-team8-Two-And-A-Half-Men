package com.project.backend.controllers;

import com.project.backend.DTO.ActivateRequestDTO;
import com.project.backend.DTO.RegistretionDTO;
import com.project.backend.DTO.UserLoginDTO;
import com.project.backend.DTO.UserLoginRequestDTO;
import com.project.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    @Autowired
    private AuthService authService;

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
        try {
            authService.registerCustomer(userData);
            return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email to verify your account."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error"));
        }
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
    @PostMapping("/activate")
    public ResponseEntity<Map<String,String>> activate(@RequestBody ActivateRequestDTO token){
        try {
            String message =  authService.activateAccount(token);
            return ResponseEntity.status(201).body(Map.of("message", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
}