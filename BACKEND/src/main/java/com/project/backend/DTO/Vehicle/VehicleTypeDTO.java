package com.project.backend.DTO.Vehicle;

import com.project.backend.models.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTypeDTO {
    private Long id;
    private String typeName;
    private String description;
    private double price;

    public VehicleTypeDTO(VehicleType vehicleType) {
        this.id = vehicleType.getId();
        this.typeName = vehicleType.getTypeName();
        this.description = vehicleType.getDescription();
        this.price = vehicleType.getPrice();
    }
}