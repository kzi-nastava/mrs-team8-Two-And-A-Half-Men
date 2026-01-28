package com.project.backend.DTO.Ride;

import com.project.backend.models.Location;
import com.project.backend.models.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideTrackingDTO {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private List<Location> stops;
    private LocalDateTime startTime;
    private RideStatus status;
}
