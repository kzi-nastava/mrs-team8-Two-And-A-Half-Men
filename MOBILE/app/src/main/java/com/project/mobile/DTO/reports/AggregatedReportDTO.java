package com.project.mobile.DTO.reports;

import java.util.List;

public class AggregatedReportDTO {
    private List<AggregatedUserReportDTO> userReports;
    private RideReportDTO combinedStats;

    public AggregatedReportDTO() {}

    public List<AggregatedUserReportDTO> getUserReports() { return userReports; }
    public void setUserReports(List<AggregatedUserReportDTO> userReports) {
        this.userReports = userReports;
    }

    public RideReportDTO getCombinedStats() { return combinedStats; }
    public void setCombinedStats(RideReportDTO combinedStats) { this.combinedStats = combinedStats; }
}
