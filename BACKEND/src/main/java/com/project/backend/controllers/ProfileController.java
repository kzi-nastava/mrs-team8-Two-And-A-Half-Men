package com.project.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @GetMapping
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PatchMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
}