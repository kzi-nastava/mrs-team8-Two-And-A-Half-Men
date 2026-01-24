package com.project.backend.service;

import com.project.backend.models.AppUser;
import com.project.backend.DTO.Ride.RideCancelationDTO;


public interface ICancellationService {
    void cancelRide(Long rideId, RideCancelationDTO reason, AppUser user);
}
