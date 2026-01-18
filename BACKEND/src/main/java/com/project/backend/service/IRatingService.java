package com.project.backend.service;

import com.project.backend.DTO.RatingRequestDTO;
import com.project.backend.DTO.RatingResponseDTO;

public interface IRatingService {
    RatingResponseDTO rateRide(RatingRequestDTO request);
}
