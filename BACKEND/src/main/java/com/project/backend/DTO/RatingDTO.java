package com.project.backend.DTO;

import java.time.LocalDateTime;

public class RatingDTO {
    private Long ratingId;
    private Long rideId;
    private Integer vehicleRating;
    private Integer driverRating;
    private String comment;
    private LocalDateTime ratedAt;
    private String ratedBy; // ime putnika koji je ocenio

    public RatingDTO() {
    }

    public RatingDTO(Long ratingId, Long Id, Integer vehicleRating,
                                 Integer driverRating, String comment,
                                 LocalDateTime ratedAt, String ratedBy) {
        this.ratingId = ratingId;
        this.rideId = Id;
        this.vehicleRating = vehicleRating;
        this.driverRating = driverRating;
        this.comment = comment;
        this.ratedAt = ratedAt;
        this.ratedBy = ratedBy;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
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

    public LocalDateTime getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(LocalDateTime ratedAt) {
        this.ratedAt = ratedAt;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }
}