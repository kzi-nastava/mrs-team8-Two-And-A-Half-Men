package com.project.backend.service;

import com.project.backend.DTO.RatingRequestDTO;
import com.project.backend.DTO.RatingResponseDTO;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;

public interface IRatingService {
    RatingResponseDTO rateRide(RatingRequestDTO request) throws ResourceNotFoundException, BadRequestException;
}
