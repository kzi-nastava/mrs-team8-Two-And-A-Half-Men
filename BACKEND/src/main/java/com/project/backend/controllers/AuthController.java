package com.project.backend.controllers;

import com.project.backend.DTO.Auth.*;
import com.project.backend.service.AuthService;
import com.project.backend.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthUtils authUtils;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        var currentUser = authUtils.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of(
                "id", currentUser.getId(),
                "email", currentUser.getEmail(),
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "role", currentUser.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDTO credentials) {

        try {
            UserTokenDTO loginResponse = authService.login(credentials);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error" + e.getMessage()));
        }
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerDriver(@RequestBody RegisterDriverDTO body) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerDriver(body));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, Object> emailData) {
        String email = (String) emailData.get("email");
        authService.forgetPassword(email);
        return ResponseEntity.ok(Map.of("message", "Successfully sent password reset instructions to your email."));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordData) {
         authService.resetPassword(resetPasswordData);
        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
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