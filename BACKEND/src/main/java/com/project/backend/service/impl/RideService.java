package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService implements IRideService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        return RideMapper.convertToHistoryResponseDTO(ride);
    }

    public RideResponseDTO getActiveRideByDriverId(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver with id " + driverId + " not found"));

        Ride activeRide = rideRepository
                .findFirstByDriverAndStatusIn(
                        driver,
                        List.of(RideStatus.ACCEPTED, RideStatus.ACTIVE)
                )
                .orElse(null);

        if (activeRide == null) {
            return null;
        }

        return RideMapper.convertToHistoryResponseDTO(activeRide);
    }
}
