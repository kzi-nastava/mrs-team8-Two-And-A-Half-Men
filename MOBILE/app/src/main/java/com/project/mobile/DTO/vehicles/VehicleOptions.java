package com.project.mobile.DTO.vehicles;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleOptions {
    private List<VehicleType> vehicleTypes;
    private List<AdditionalService> additionalServices;
}
