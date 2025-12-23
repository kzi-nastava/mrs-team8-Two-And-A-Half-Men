package com.project.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> credentials) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> userData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
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