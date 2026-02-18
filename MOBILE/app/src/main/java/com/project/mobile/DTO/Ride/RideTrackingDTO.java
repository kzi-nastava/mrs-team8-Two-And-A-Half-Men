package com.project.mobile.DTO.Ride;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideTrackingDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("driverId")
    private Long driverId;

    @SerializedName("passengerId")
    private Long passengerId;

    @SerializedName("stops")
    private List<RouteItemDTO> stops;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("status")
    private String status;

    public RideTrackingDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public List<RouteItemDTO> getStops() {
        return stops;
    }

    public void setStops(List<RouteItemDTO> stops) {
        this.stops = stops;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
