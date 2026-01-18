package com.project.backend.DTO;

import com.project.backend.models.enums.RideStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private String cancellationReason;
    private double cost;
    private String path;
}
