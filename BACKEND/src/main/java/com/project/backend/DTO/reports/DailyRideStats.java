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
public class DailyRideStats {
    private LocalDate date;
    private Long numberOfRides;
    private Double totalDistance; // in kilometers
    private Double totalAmount;   // money spent/earned
}