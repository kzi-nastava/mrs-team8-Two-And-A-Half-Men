package com.project.mobile.DTO.reports;

public class DailyRideStats {
    private String date;
    private int numberOfRides;
    private double totalDistance;
    private double totalAmount;

    public DailyRideStats() {}

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getNumberOfRides() { return numberOfRides; }
    public void setNumberOfRides(int numberOfRides) { this.numberOfRides = numberOfRides; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
