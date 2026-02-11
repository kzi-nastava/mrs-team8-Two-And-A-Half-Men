package com.project.backend.service;

import com.project.backend.DTO.Ride.*;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.actor.PassengerActor;

import java.util.List;
import java.util.Map;

public interface IRideService {
    RideResponseDTO getRideById(Long id);

    Map<String, Object> startARide(String id, Long id1);

    CostTimeDTO estimateRide(RideBookingParametersDTO rideData);

    RideResponseDTO getActiveRideByDriverId(Long id);

    RideResponseDTO getActiveRideByCustomerId(Long id);

    NoteResponseDTO saveRideNote(Long rideId, PassengerActor actor, NoteRequestDTO noteRequest);

    RideTrackingDTO getRideTrackingInfo(PassengerActor actor);

    RideTrackingDTO getDriversActiveRide(Driver driver);

    void sendRideUpdate(Ride ride);

    CostTimeDTO endRideById(Long id, Driver driver);

    void finishRide(Long id, FinishRideDTO finishRideDTO);

    List<RideResponseDTO> getAllBookedRidesByCustomer(Customer customer);

    List<RideResponseDTO> getActiveRides(String driverName);

    RideTrackingDTO getRideTrackingById(Long id, AppUser user);
}
