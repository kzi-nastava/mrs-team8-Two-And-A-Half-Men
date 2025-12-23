package com.project.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @GetMapping("/active")
    public ResponseEntity<?> getActiveVehicles() {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
}