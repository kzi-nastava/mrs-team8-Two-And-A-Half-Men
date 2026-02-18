package com.project.backend.service;

import com.project.backend.DTO.Ride.NewRideDTO;
import com.project.backend.DTO.Ride.RideBookingParametersDTO;

public interface RideBookingService {
    NewRideDTO bookRide(Long userId, RideBookingParametersDTO body);

    void findDriverForScheduledRide(Long rideId, long minutesBefore);
}
