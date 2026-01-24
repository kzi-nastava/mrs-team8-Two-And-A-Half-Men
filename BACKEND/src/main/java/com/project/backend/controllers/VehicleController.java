package com.project.backend.controllers;

import com.project.backend.DTO.Vehicle.ActiveVehicleDTO;
import com.project.backend.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveVehicleDTO>> getActiveVehicles() {
        // Dummy data
        List<ActiveVehicleDTO> vehicles = new ArrayList<>();

        vehicles.add(new ActiveVehicleDTO(
                1L,
                "Toyota Corolla",
                "BG-123-AB",
                "Sedan",
                44.787197,
                20.457273,
                false
        ));

        vehicles.add(new ActiveVehicleDTO(
                2L,
                "Volkswagen Passat",
                "NS-456-CD",
                "Sedan",
                45.267136,
                19.833549,
                true
        ));

        vehicles.add(new ActiveVehicleDTO(
                3L,
                "BMW X5",
                "BG-789-EF",
                "SUV",
                44.818611,
                20.468056,
                false
        ));

        vehicles.add(new ActiveVehicleDTO(
                4L,
                "Mercedes E-Class",
                "BG-321-GH",
                "Sedan",
                44.772182,
                20.493896,
                true
        ));

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/options")
    public ResponseEntity<?> getVehicleOptions() {
        return ResponseEntity.ok(vehicleService.getVehicleOptions());
    }
}