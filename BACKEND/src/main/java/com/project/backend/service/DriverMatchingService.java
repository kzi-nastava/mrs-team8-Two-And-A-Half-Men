package com.project.backend.service;

import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.models.Ride;

import java.util.Optional;

public interface DriverMatchingService {
    /**
     * Looks for a suitable driver for a specific ride
     * @param ride ride for whom a driver is being searched for
     * @return data about found driver and estimated distance for his arrival
     */
    public Optional<FindDriverDTO> findDriverFor(Ride ride);

    /**
     * Looks for a suitable driver for with some filters applied
     * @param filter filters that driver must satisfy in order to be suitable
     * @return the driver that satisfies all filters and is closest to the starting point of a new ride
     */
    public Optional<FindDriverDTO> findBestDriver(FindDriverFilter filter);
}
