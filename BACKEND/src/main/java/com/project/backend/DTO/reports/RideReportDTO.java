package com.project.backend.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RideReportDTO {
    private List<DailyRideStats> dailyStats;

    // Cumulative totals for the entire period
    private Long totalRides;
    private Double totalDistance;
    private Double totalAmount;

    // Averages for the period
    private Double averageRidesPerDay;
    private Double averageDistancePerDay;
    private Double averageAmountPerDay;
    private Double averageDistancePerRide;
    private Double averageAmountPerRide;
}