package com.project.backend.service;

import com.project.backend.DTO.RatingRequestDTO;
import com.project.backend.DTO.RatingResponseDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface IRatingService {
    RatingResponseDTO rateRide(RatingRequestDTO request) throws ChangeSetPersister.NotFoundException, BadRequestException;
}
