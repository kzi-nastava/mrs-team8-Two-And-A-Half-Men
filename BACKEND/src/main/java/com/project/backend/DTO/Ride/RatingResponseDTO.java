package com.project.backend.DTO.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private Long passengerId;
    private Long rideId;
    private Integer vehicleRating;
    private Integer driverRating;
    private String comment;
}
