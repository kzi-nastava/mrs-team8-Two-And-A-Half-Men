package com.project.backend.geolocation.coordinates;

import com.project.backend.geolocation.metrics.MetricsDistance;
import com.project.backend.geolocation.utils.Constants;

public abstract class Coordinates {
    protected double latitude;
    protected double longitude;

    public Coordinates(){}
    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Coordinates(String address){
        setCoordinate(address);
    }
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public abstract void setCoordinate(String address);
    public void setCoordinate(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public abstract String getAddress();
    public double distanceAirLine(Coordinates other ) {
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Constants.EARTH_RADIUS_M * c;
    }
    public double distanceAirLine(Coordinates other, MetricsDistance metric ) {
        return metric.fromMeters(this.distanceAirLine(other));
    }
    public abstract double GetTimeMap(Coordinates other);
    public abstract double GetDistanceMap(Coordinates other);
    public abstract double[] GetRouteMap(Coordinates other);
}
