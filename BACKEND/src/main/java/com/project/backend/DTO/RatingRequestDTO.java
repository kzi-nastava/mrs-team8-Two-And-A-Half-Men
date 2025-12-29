package com.project.backend.DTO;

public class RatingRequestDTO {
    private Integer vehicleRating;
    private Integer driverRating;
    private String comment;
    private String ratedBy;

    public RatingRequestDTO() {
    }

    public RatingRequestDTO(Integer vehicleRating, Integer driverRating, String comment, String ratedBy) {
        this.vehicleRating = vehicleRating;
        this.driverRating = driverRating;
        this.comment = comment;
        this.ratedBy = ratedBy;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }
}