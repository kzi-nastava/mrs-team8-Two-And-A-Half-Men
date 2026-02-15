package com.project.mobile.DTO.Ride;

public class RatingResponseDTO {
    private Long passengerId;
    private Long rideId;

    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;

    public RatingResponseDTO() {
    }

    public RatingResponseDTO(Long passengerId, Integer driverRating, Integer vehicleRating, String comment, Long rideId) {
        this.passengerId = passengerId;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this .rideId = rideId;
    }

    public Long getId() {
        return passengerId;
    }

    public void setId(Long passengerId) {
        this.passengerId = passengerId;
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

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
}