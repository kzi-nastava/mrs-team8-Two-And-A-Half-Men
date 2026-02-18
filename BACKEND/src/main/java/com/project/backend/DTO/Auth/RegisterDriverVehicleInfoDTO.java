package com.project.backend.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverVehicleInfoDTO {
    @NotBlank(message = "Type ID is required")
    private Long typeId;

    @Positive(message = "Number of seats must be positive")
    private Integer numberOfSeats;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "License plate is required")
    private String licensePlate;
    @NotNull(message = "Additional services list cannot be null")
    private List<Long> additionalServicesIds;
}
