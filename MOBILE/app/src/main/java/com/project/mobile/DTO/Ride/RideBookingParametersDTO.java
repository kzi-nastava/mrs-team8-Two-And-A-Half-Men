package com.project.mobile.DTO.Ride;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * DTO for booking a ride
 * Matches the web's BookRideRequest interface
 */
public class RideBookingParametersDTO {

    @SerializedName("route")
    private List<RouteItemDTO> route;

    @SerializedName("routeId")
    private Long routeId;

    @SerializedName("scheduledTime")
    private String scheduledTime;

    @SerializedName("passengers")
    private List<String> passengers;

    @SerializedName("vehicleTypeId")
    private Long vehicleTypeId;

    @SerializedName("additionalServicesIds")
    private List<Long> additionalServicesIds;

    // Constructors
    public RideBookingParametersDTO() {
    }

    // Getters and Setters
    public List<RouteItemDTO> getRoute() {
        return route;
    }

    public void setRoute(List<RouteItemDTO> route) {
        this.route = route;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
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
