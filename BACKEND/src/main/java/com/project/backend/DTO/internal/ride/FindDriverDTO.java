package com.project.backend.DTO.internal.ride;

import com.project.backend.models.Driver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindDriverDTO {
    private Driver driver;
    private Double estimatedDistance;
}
