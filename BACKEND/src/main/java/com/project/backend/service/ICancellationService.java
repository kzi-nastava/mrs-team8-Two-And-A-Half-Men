package com.project.backend.service;

import com.project.backend.DTO.Ride.RideCancelationDTO;
import com.project.backend.models.AppUser;

public interface ICancellationService {
    void cancelRide(Long rideId, RideCancelationDTO reason, AppUser user);
}
