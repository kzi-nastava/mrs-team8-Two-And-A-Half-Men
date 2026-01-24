package com.project.backend.geolocation.metrics;

public enum MetricsTime {
    MILLISECONDS(0.001),
    SECONDS(1.0),
    MINUTES(60.0),
    HOURS(3600.0),
    DAYS(86400.0),
    WEEKS(604800.0),
    YEARS(31536000.0);


    private final double secondsFactor;

    MetricsTime(double secondsFactor) {
        this.secondsFactor = secondsFactor;
    }

    public double toSeconds(double value) {
        return value * secondsFactor;
    }

    public double fromSeconds(double seconds) {
        return seconds / secondsFactor;
    }

}
