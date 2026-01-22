package com.project.backend.service;

import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;

public interface IRideService {
    RideResponseDTO getRideById(Long id);

    Object createRide(Long id, RideBookingParametersDTO body);
}
