package com.project.backend.DTO.Ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CostTimeDTO {
    private double cost;
    private double time; // in minutes
}
