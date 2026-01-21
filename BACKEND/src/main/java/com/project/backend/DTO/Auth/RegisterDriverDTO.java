package com.project.backend.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverDTO {
    private RegisterDriverPersonalInfoDTO personalInfo;
    private RegisterDriverVehicleInfoDTO vehicleInfo;
}

