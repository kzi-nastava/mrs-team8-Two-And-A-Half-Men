package com.project.backend.DTO.Ride;

import com.project.backend.DTO.LocationDTO;
import com.project.backend.DTO.PassengerDTO;
import com.project.backend.models.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideResponseDTO {
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime scheduledTime;

    private String driverName;
    private String rideOwnerName;
    private Long rideOwnerId;

    private RideStatus status;
    private String path;
    private String cancellationReason;
    private Double price;
    private Double totalCost;

    private List<String> additionalServices;
    private List<PassengerDTO> passengers;
    private List<LocationDTO> locations;
    private Long routeId;

    private boolean isFavourite;
}
