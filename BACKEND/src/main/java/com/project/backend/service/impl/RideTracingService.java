package com.project.backend.service.impl;

import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.repositories.redis.DriverLocationsRepository;
import com.project.backend.service.IRideTracing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@Service
public class RideTracingService implements IRideTracing {

    @Autowired
    private DriverLocationsRepository locationsRepository;
    @Autowired
    private LocationTransformer transformer;

    @Override
    public void setRideLocation(Long driverID, Double longitude, Double latitude, Boolean isActive) {
        if(isActive) {
            locationsRepository.setActiveDriveRoute(driverID, new Point(longitude, latitude));
        }
    }
    public String finishRoute(Long driverID)
    {
        String route = locationsRepository.getActiveDriveRoute(driverID);
        locationsRepository.clearActiveDriveRoute(driverID);
        return route;
    }
    public String getActiveRoute(Long driverID)
    {
        return locationsRepository.getActiveDriveRoute(driverID);
    }
}
