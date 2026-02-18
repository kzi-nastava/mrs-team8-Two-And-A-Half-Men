package com.project.backend.DTO.Ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewRideDTO {
    private Long id;
    private String status;
    private Double estimatedDistance;
}
