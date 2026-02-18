package com.project.backend.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideReportRequest {
    private LocalDate startDate;
    private LocalDate endDate;

    // For admin queries - optional
    private Long userId;        // Specific user (driver or passenger)
    private String userType;    // "DRIVER" or "PASSENGER" - for aggregated view
}