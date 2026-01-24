package com.project.backend.geolocation.coordinates.openstreet;

import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;

public class OpenStreetCoordinatesFactory extends CoordinatesFactory {
    public OpenStreetCoordinatesFactory() {}
    @Override
    public Coordinates getCoordinate(String address) {
        return new OpenStreetCoordinates(address);
    }

    @Override
    public Coordinates getCoordinate(double latitude, double longitude) {
        return new OpenStreetCoordinates(latitude, longitude);
    }
}
