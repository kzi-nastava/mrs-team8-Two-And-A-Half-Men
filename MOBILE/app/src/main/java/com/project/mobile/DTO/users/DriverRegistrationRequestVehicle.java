package com.project.mobile.DTO.users;

import java.util.List;

public class DriverRegistrationRequestVehicle {
    private String model;
    private String licensePlate;
    private int numberOfSeats;
    private long typeId;
    private List<Long> additionalServicesIds;

    public DriverRegistrationRequestVehicle() {}

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public long getTypeId() { return typeId; }
    public void setTypeId(long typeId) { this.typeId = typeId; }

    public List<Long> getAdditionalServicesIds() { return additionalServicesIds; }
    public void setAdditionalServicesIds(List<Long> additionalServicesIds) {
        this.additionalServicesIds = additionalServicesIds;
    }
}
