package com.project.backend.geolocation.coordinates;

public abstract class CoordinatesFactory {
    public abstract Coordinates getCoordinate(String address);
    public abstract Coordinates getCoordinate(double latitude, double longitude);

}
