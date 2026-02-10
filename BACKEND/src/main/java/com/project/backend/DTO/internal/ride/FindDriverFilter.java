package com.project.backend.DTO.internal.ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindDriverFilter {
    double latitude;
    double longitude;
    int numberOfPassengers = 1;
    Long vehicleTypeId;
    List<Long> additionalServicesIds;
}
