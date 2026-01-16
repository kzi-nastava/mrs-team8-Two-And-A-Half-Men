package com.project.backend.DTO.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionsDTO {
    List<VehicleTypeDTO> vehicleTypes;
    List<AdditionalServiceDTO> additionalServices;
}
