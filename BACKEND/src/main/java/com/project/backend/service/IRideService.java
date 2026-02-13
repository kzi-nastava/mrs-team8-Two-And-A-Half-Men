package com.project.backend.service;

import com.project.backend.DTO.Ride.*;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.actor.PassengerActor;

import java.util.List;
import java.util.Map;

public interface IRideService {
    RideResponseDTO getRideById(Long id, Long currentUserId);

    Map<String, Object> startARide(String id, Long id1);

    CostTimeDTO estimateRide(RideBookingParametersDTO rideData);

    NoteResponseDTO saveRideNote(Long rideId, PassengerActor actor, NoteRequestDTO noteRequest);

    CostTimeDTO endRideById(Long id, Driver driver);

    RideResponseDTO finishRide(Long id, FinishRideDTO finishRideDTO);

    List<RideResponseDTO> getAllBookedRidesByCustomer(Customer customer);

    List<RideResponseDTO> getActiveRides(String driverName);

    List<RideResponseDTO> getMyRides(AppUser currentUser);
}
