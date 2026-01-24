package com.project.backend.DTO.Ride;

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
public class RideBookingParametersDTO {
    private Long routeId;
    private List<RouteItemDTO> route;
    private LocalDateTime scheduledTime;
    private List<String> passengers;
    private Long vehicleTypeId;
    private List<Long> additionalServicesIds;
}
