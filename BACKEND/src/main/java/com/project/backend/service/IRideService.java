package com.project.backend.service;

import com.project.backend.DTO.Ride.*;
import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.actor.PassengerActor;

import java.util.Map;

public interface IRideService {
    RideResponseDTO getRideById(Long id);

    Object createRide(Long id, RideBookingParametersDTO body);

    Map<String, Object> startARide(String id, Long id1);

    CostTimeDTO estimateRide(RideBookingParametersDTO rideData);

    RideResponseDTO getActiveRideByDriverId(Long id);

    RideResponseDTO getActiveRideByCustomerId(Long id);

    NoteResponseDTO saveRideNote(Long rideId, PassengerActor actor, NoteRequestDTO noteRequest);

    RideTrackingDTO getRideTrackingInfo(PassengerActor actor);

    public void sendRideUpdate(Ride ride);
    CostTimeDTO endRideById(Long id, Driver driver);
}
