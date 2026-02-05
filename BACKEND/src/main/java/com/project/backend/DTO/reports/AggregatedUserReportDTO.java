package com.project.backend.DTO.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedUserReportDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private RideReportDTO report;
}