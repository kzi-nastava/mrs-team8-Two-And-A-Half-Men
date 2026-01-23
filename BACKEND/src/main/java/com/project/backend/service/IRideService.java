package com.project.backend.service;

import com.project.backend.DTO.Ride.NoteRequestDTO;
import com.project.backend.DTO.Ride.NoteResponseDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.Ride.RideTrackingDTO;
import com.project.backend.models.Ride;

public interface IRideService {
    RideResponseDTO getRideById(Long id);

    RideResponseDTO getActiveRideByDriverId(Long id);

    RideResponseDTO getActiveRideByCustomerId(Long id);

    NoteResponseDTO saveRideNote(Long rideId, Long passengerId, NoteRequestDTO noteRequest);

    RideTrackingDTO getRideTrackingInfo(Long rideId);

    public void sendRideUpdate(Ride ride);
}
