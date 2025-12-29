package com.project.mobile.models;

import java.util.List;

public class VehicleInfo {
    private String type;
    private int numberOfSeats;
    private String model;
    private String plateNumber;
    private List<String> additionalServices;

    public VehicleInfo() {}

    public VehicleInfo(String type, int numberOfSeats, String model,
                       String plateNumber, List<String> additionalServices) {
        this.type = type;
        this.numberOfSeats = numberOfSeats;
        this.model = model;
        this.plateNumber = plateNumber;
        this.additionalServices = additionalServices;
    }

    // Getters
    public String getType() { return type; }
    public int getNumberOfSeats() { return numberOfSeats; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
    public List<String> getAdditionalServices() { return additionalServices; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public void setModel(String model) { this.model = model; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public void setAdditionalServices(List<String> additionalServices) {
        this.additionalServices = additionalServices;
    }
}