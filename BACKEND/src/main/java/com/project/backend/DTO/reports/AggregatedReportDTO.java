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
public class AggregatedReportDTO {
    // Individual user reports
    private List<AggregatedUserReportDTO> userReports;

    // Combined/aggregated stats for all users
    private RideReportDTO combinedStats;
}