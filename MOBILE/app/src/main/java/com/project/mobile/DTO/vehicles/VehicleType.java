package com.project.mobile.DTO.vehicles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleType {
    private long id;
    private String typeName;
    private String description;
    private double price;
}
