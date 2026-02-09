package com.project.mobile.DTO.Ride;

public class RideBookedDTO {
    private Long id;
    private String startTime;
    private String scheduleTime;
    private String route;
    private String driverName;
    private String status;

    public RideBookedDTO() {
    }

    public RideBookedDTO(Long id, String startTime, String scheduleTime, String route, String driverName, String status) {
        this.id = id;
        this.startTime = startTime;
        this.scheduleTime = scheduleTime;
        this.route = route;
        this.driverName = driverName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
