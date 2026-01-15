package com.project.backend.controllers;

import com.project.backend.DTO.Profile.UpdateProfileRequestDTO;
import com.project.backend.models.AppUser;
import com.project.backend.service.ProfileService;
import com.project.backend.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    @Autowired
    AuthUtils authUtils;

    @Autowired
    ProfileService profileService;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        AppUser user = authUtils.getCurrentUser();
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "error", "Unauthorized"));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profileService.getProfile(user.getId(), user.getRole()));
    }

    @PatchMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequestDTO body) {
        AppUser user = authUtils.getCurrentUser();
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("ok", false, "error", "Unauthorized"));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profileService.updateProfile(user.getId(), user.getRole(), body));
    }
}