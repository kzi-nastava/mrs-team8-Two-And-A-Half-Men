package com.project.backend.service;

public interface IRideTracing {
    void setRideLocation(Long driverID, Double longitude, Double latitude, Boolean isActive);
}
