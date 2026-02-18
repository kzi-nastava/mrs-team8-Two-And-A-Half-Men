package com.project.backend.geolocation.metrics;

public enum MetricsDistance {
    MILLIMETERS(0.001),
    INCHES(0.0254),
    METERS(1.0),
    KILOMETERS(1000.0),
    MILES(1609.34),
    FEET(0.3048),
    YARDS(0.9144),
    NAUTICAL_MILES(1852.0);

    private final double metersFactor;

    MetricsDistance(double metersFactor) {
        this.metersFactor = metersFactor;
    }
    public double toMeters(double value) {
        return value * metersFactor;
    }
    public double fromMeters(double meters) {
        return meters / metersFactor;
    }
}
