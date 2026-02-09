package com.project.mobile.DTO;

import com.google.gson.annotations.SerializedName;

public class CostTimeDTO {
    @SerializedName("cost")
    private double cost;

    @SerializedName("time")
    private double time;

    public CostTimeDTO() {
    }

    public CostTimeDTO(double cost, double time) {
        this.cost = cost;
        this.time = time;
    }

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
}
