package com.project.mobile.DTO.reports;

public class AggregatedUserReportDTO {
    private long userId;
    private String userName;
    private String userEmail;
    private RideReportDTO report;

    public AggregatedUserReportDTO() {}

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public RideReportDTO getReport() { return report; }
    public void setReport(RideReportDTO report) { this.report = report; }
}
