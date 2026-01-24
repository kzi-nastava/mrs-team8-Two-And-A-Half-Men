package com.project.backend.service;

import com.project.backend.DTO.DriverLocationDTO;

import java.util.List;

public interface IDriverLocationService {

    void updateDriverLocation(Long driverId, DriverLocationDTO locationDTO);

    DriverLocationDTO getDriverLocation(Long driverId);

    List<DriverLocationDTO> getAllDriverLocations();

    List<DriverLocationDTO> getNearbyDrivers(double longitude, double latitude, double radiusKm);

    void removeDriverLocation(Long driverId);

    List<DriverLocationDTO> getAvailableDriversNearby(double longitude, double latitude, double radiusKm);
}
