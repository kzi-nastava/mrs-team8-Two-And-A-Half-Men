package com.project.mobile.DTO.Ride;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("scheduledTime")
    private String scheduledTime;

    @SerializedName("driverName")
    private String driverName;

    @SerializedName("driverId")
    private Long driverId;

    @SerializedName("rideOwnerName")
    private String rideOwnerName;

    @SerializedName("rideOwnerId")
    private Long rideOwnerId;

    @SerializedName("status")
    private String status;

    @SerializedName("path")
    private String path;

    @SerializedName("cancellationReason")
    private String cancellationReason;

    @SerializedName("price")
    private Double price;

    @SerializedName("totalCost")
    private Double totalCost;

    @SerializedName("additionalServices")
    private List<String> additionalServices;

    @SerializedName("passengers")
    private List<PassengerDTO> passengers;

    @SerializedName("locations")
    private List<RouteItemDTO> locations;

    @SerializedName("routeId")
    private Long routeId;

    @SerializedName("favourite")
    private boolean isFavourite;

    // Constructors
    public RideDTO() {
    }

    // Getters and Setters
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getRideOwnerName() {
        return rideOwnerName;
    }

    public void setRideOwnerName(String rideOwnerName) {
        this.rideOwnerName = rideOwnerName;
    }

    public Long getRideOwnerId() {
        return rideOwnerId;
    }

    public void setRideOwnerId(Long rideOwnerId) {
        this.rideOwnerId = rideOwnerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public List<String> getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(List<String> additionalServices) {
        this.additionalServices = additionalServices;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }

    public List<RouteItemDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteItemDTO> locations) {
        this.locations = locations;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
