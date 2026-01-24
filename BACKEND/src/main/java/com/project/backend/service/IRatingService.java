package com.project.backend.service;

import com.project.backend.DTO.Ride.RatingRequestDTO;
import com.project.backend.DTO.Ride.RatingResponseDTO;

public interface IRatingService {
    RatingResponseDTO rateRide(RatingRequestDTO request);
}
