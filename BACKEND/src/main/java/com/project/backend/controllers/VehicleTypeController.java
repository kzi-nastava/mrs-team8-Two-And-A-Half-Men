package com.project.backend.controllers;

import com.project.backend.DTO.PricingUpdateDTO;
import com.project.backend.DTO.VehicleTypeDTO;
import com.project.backend.service.IVehicleTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {
    private final IVehicleTypeService vehicleTypeService;

    @GetMapping
    public ResponseEntity<List<VehicleTypeDTO>> getAllVehicleTypes() {
        List<VehicleTypeDTO> types = vehicleTypeService.getAllVehicleTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeDTO> getVehicleType(@PathVariable Long id) {
        VehicleTypeDTO type = vehicleTypeService.getVehicleType(id);
        return ResponseEntity.ok(type);
    }

    @PatchMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleTypeDTO> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody PricingUpdateDTO pricingUpdateDTO
            ) {
        VehicleTypeDTO type = vehicleTypeService.updatePricing(id, pricingUpdateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(type);
    }
}
