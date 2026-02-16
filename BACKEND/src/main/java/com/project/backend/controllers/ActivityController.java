package com.project.backend.controllers;

import com.project.backend.models.Driver;
import com.project.backend.service.AuthService;
import com.project.backend.service.impl.ActivityDriverService;
import com.project.backend.util.AuthUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/driver/working")
public class ActivityController {
    private final ActivityDriverService activityDriverService;
    private final AuthUtils authUtils;

    @PatchMapping("/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> startWorking() {
        Driver driver = authUtils.getCurrentDriver();
        if(driver == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        activityDriverService.activateDriver(driver);
        return ResponseEntity.ok().body(Map.of("message", "Driver started working"));
    }

    @PatchMapping("/stop")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> stopWorking() {
        Driver driver = authUtils.getCurrentDriver();
        if(driver == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        activityDriverService.deActivateDriver(driver);
        return ResponseEntity.ok().body(Map.of("message", "Driver stopped working"));
    }
}
