package com.project.backend.controllers;

import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Autowired
    AuthUtils authUtils;
    @GetMapping
    public ResponseEntity<?> getProfile() {
        Customer user = authUtils.getCurrentCustomer();
        if(user != null){
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getAuthorities(),
                    "email", user.getEmail()
            ));
        }

        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PatchMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
}