package com.project.mobile.DTO.Ride;

import com.google.gson.annotations.SerializedName;

public class PassengerDTO {
    @SerializedName("email")
    private String email;

    @SerializedName("driverRating")
    private Integer driverRating;

    @SerializedName("vehicleRating")
    private Integer vehicleRating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("inconsistencyNote")
    private String inconsistencyNote;

    public PassengerDTO() {
    }

    public PassengerDTO(String email, Integer driverRating, Integer vehicleRating, String comment, String inconsistencyNote) {
        this.email = email;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.inconsistencyNote = inconsistencyNote;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInconsistencyNote() {
        return inconsistencyNote;
    }

    public void setInconsistencyNote(String inconsistencyNote) {
        this.inconsistencyNote = inconsistencyNote;
    }
}