package com.project.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTypeDTO {
    private Long id;
    private String typeName;
    private String description;
    private Double price;
}
