package com.project.mobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ride {
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

    @SerializedName("rideOwnerName")
    private String rideOwnerName;

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

    @SerializedName("addresses")
    private List<String> addresses;

    @SerializedName("passengersMails")
    private List<String> passengersMails;

    // Constructors
    public Ride() {
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

    public String getRideOwnerName() {
        return rideOwnerName;
    }

    public void setRideOwnerName(String rideOwnerName) {
        this.rideOwnerName = rideOwnerName;
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

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getPassengersMails() {
        return passengersMails;
    }

    public void setPassengersMails(List<String> passengersMails) {
        this.passengersMails = passengersMails;
    }
}