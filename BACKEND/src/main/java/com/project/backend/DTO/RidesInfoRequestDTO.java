package com.project.backend.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RidesInfoRequestDTO {

    private String vehicleType;
    private ArrayList<String> additionalServices;
    private ArrayList<String> addressesPoints;
    private LocalDateTime scheduledTime;
    private LocalDateTime scheduledAt;

    public RidesInfoRequestDTO() {
    }

    public RidesInfoRequestDTO(String vehicleType, ArrayList<String> additionalServices,
                               ArrayList<String> addressPoints, LocalDateTime scheduledTime) {
        this.vehicleType = vehicleType;
        this.additionalServices = additionalServices;
        this.addressesPoints = addressPoints;
        this.scheduledTime = scheduledTime;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public ArrayList<String> getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(ArrayList<String> additionalServices) {
        this.additionalServices = additionalServices;
    }

    public ArrayList<String> getAddressesPoints() {
        return addressesPoints;
    }

    public void setAddressPoints(ArrayList<String> addressPoints) {
        this.addressesPoints = addressPoints;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

}
