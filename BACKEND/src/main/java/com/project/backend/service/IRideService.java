package com.project.backend.service;

import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;

import java.util.Map;

public interface IRideService {
    RideResponseDTO getRideById(Long id);

    Object createRide(Long id, RideBookingParametersDTO body);

    Map<String, Object> startARide(String id, Long id1);
}
