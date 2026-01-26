package com.project.backend.service;

import com.project.backend.DTO.Ride.RatingRequestDTO;
import com.project.backend.DTO.Ride.RatingResponseDTO;
import com.project.backend.models.AppUser;
import com.project.backend.service.rating.RatingActor;

public interface IRatingService {
    RatingResponseDTO rateRide(Long rideId, RatingActor actor, RatingRequestDTO request);
}
