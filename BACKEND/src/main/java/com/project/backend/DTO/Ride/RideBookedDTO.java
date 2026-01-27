package com.project.backend.DTO.Ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideBookedDTO {
    Long id;
    String startTime;
    String scheduleTime;
    String route;
    String driverName;
    String status;
}
