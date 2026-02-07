package com.project.mobile.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideBookingParametersDTO {
    @SerializedName("routeId")
    private Long routeId;

    @SerializedName("route")
    private List<RouteItemDTO> route;

    @SerializedName("scheduledTime")
    private String scheduledTime; // Use String for LocalDateTime (ISO 8601 format)

    @SerializedName("passengers")
    private List<String> passengers;

    @SerializedName("vehicleTypeId")
    private Long vehicleTypeId;

    @SerializedName("additionalServicesIds")
    private List<Long> additionalServicesIds;

    // Constructors
    public RideBookingParametersDTO() {
    }

    public RideBookingParametersDTO(Long routeId, List<RouteItemDTO> route, String scheduledTime,
                                    List<String> passengers, Long vehicleTypeId,
                                    List<Long> additionalServicesIds) {
        this.routeId = routeId;
        this.route = route;
        this.scheduledTime = scheduledTime;
        this.passengers = passengers;
        this.vehicleTypeId = vehicleTypeId;
        this.additionalServicesIds = additionalServicesIds;
    }

    // Getters and Setters
    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public List<RouteItemDTO> getRoute() {
        return route;
    }

    public void setRoute(List<RouteItemDTO> route) {
        this.route = route;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public List<Long> getAdditionalServicesIds() {
        return additionalServicesIds;
    }

    public void setAdditionalServicesIds(List<Long> additionalServicesIds) {
        this.additionalServicesIds = additionalServicesIds;
    }
}
