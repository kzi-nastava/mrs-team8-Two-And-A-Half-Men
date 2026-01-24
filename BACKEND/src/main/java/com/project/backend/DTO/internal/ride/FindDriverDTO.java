package com.project.backend.DTO.internal.ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindDriverDTO {
    private Long driverId;
    private Double estimatedDistance;
}
