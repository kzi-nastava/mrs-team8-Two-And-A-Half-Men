package com.project.backend.service;

import com.project.backend.DTO.Ride.CostTimeDTO;
import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Ride.NoteRequestDTO;
import com.project.backend.DTO.Ride.NoteResponseDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;

import java.util.Map;

public interface IRideService {
    RideResponseDTO getRideById(Long id);

    Object createRide(Long id, RideBookingParametersDTO body);

    Map<String, Object> startARide(String id, Long id1);

    CostTimeDTO estimateRide(RideBookingParametersDTO rideData);

    RideResponseDTO getActiveRideByDriverId(Long id);

    RideResponseDTO getActiveRideByCustomerId(Long id);

    NoteResponseDTO saveRideNote(Long rideId, AppUser user, String accessToken, NoteRequestDTO noteRequest);

    RideTrackingDTO getRideTrackingInfo(Long rideId);

    public void sendRideUpdate(Ride ride);
    CostTimeDTO endRideById(Long id, Driver driver);
}
