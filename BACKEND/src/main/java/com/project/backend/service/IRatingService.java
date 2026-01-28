package com.project.backend.service;

import com.project.backend.DTO.Ride.RatingRequestDTO;
import com.project.backend.DTO.Ride.RatingResponseDTO;
import com.project.backend.models.actor.PassengerActor;

public interface IRatingService {
    RatingResponseDTO rateRide(Long rideId, PassengerActor actor, RatingRequestDTO request);
}
