package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Ride;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideService implements IRideService {
    private final RideRepository rideRepository;

    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        return RideMapper.convertToHistoryResponseDTO(ride);
    }
}
