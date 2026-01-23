package com.project.backend.DTO.Ride;

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
    private RideStatus status;
    private Double estimatedDistance;
    private Integer estimatedDuration;
    private LocalDateTime startTime;
    private List<String> stops;
}
