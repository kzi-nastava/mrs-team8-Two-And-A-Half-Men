package com.project.backend.service;

public interface IRideTracing {
    public void setRideLocation(Long driverID, Double longitude, Double latitude, Boolean isActive);

}
