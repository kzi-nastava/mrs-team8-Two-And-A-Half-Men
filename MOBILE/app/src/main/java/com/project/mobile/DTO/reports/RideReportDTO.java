package com.project.mobile.DTO.reports;

import java.util.List;

public class RideReportDTO {
    private List<DailyRideStats> dailyStats;
    private int totalRides;
    private double totalDistance;
    private double totalAmount;
    private double averageRidesPerDay;
    private double averageDistancePerDay;
    private double averageAmountPerDay;
    private double averageDistancePerRide;
    private double averageAmountPerRide;

    public RideReportDTO() {}

    public List<DailyRideStats> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyRideStats> dailyStats) { this.dailyStats = dailyStats; }

    public int getTotalRides() { return totalRides; }
    public void setTotalRides(int totalRides) { this.totalRides = totalRides; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAverageRidesPerDay() { return averageRidesPerDay; }
    public void setAverageRidesPerDay(double averageRidesPerDay) {
        this.averageRidesPerDay = averageRidesPerDay;
    }

    public double getAverageDistancePerDay() { return averageDistancePerDay; }
    public void setAverageDistancePerDay(double averageDistancePerDay) {
        this.averageDistancePerDay = averageDistancePerDay;
    }

    public double getAverageAmountPerDay() { return averageAmountPerDay; }
    public void setAverageAmountPerDay(double averageAmountPerDay) {
        this.averageAmountPerDay = averageAmountPerDay;
    }

    public double getAverageDistancePerRide() { return averageDistancePerRide; }
    public void setAverageDistancePerRide(double averageDistancePerRide) {
        this.averageDistancePerRide = averageDistancePerRide;
    }

    public double getAverageAmountPerRide() { return averageAmountPerRide; }
    public void setAverageAmountPerRide(double averageAmountPerRide) {
        this.averageAmountPerRide = averageAmountPerRide;
    }
}
