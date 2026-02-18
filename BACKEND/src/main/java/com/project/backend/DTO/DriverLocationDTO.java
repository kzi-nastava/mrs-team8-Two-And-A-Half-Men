package com.project.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocationDTO {
    private Long driverId;
    private String driverName;
    private String driverEmail;
    private Double latitude;
    private Double longitude;
    private Boolean isOccupied;
    private Boolean isActive;
    private Long currentRideId;
    private String vehicleTypeName;
    private Long timestamp;
}
