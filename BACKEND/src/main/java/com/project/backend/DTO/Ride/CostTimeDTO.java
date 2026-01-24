package com.project.backend.DTO.Ride;

public class CostTimeDTO {
    private double cost;
    private double time; // in minutes

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
    public CostTimeDTO() {
    }

    public CostTimeDTO(double cost, double time) {
        this.cost = cost;
        this.time = time;
    }
}
