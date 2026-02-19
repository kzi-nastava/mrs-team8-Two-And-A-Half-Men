package com.project.mobile.DTO.Ride;

import com.google.gson.annotations.SerializedName;

/**
 * Response DTO after successfully booking a ride
 */
public class RideBookingResponse {
    
    @SerializedName("id")
    private long id;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("estimatedDistance")
    private double estimatedDistance;

    // Constructors
    public RideBookingResponse() {
    }

    public RideBookingResponse(long id, String status, double estimatedDistance) {
        this.id = id;
        this.status = status;
        this.estimatedDistance = estimatedDistance;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }
}
