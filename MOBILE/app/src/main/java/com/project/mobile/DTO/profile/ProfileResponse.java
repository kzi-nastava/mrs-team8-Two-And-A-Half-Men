package com.project.mobile.DTO.profile;

import java.util.List;

public class ProfileResponse {
    private PersonalInfo personalInfo;
    private VehicleInfo vehicleInfo;
    private PendingChangeRequest pendingChangeRequest;

    public static class PersonalInfo {
        private long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String email;
        private String imgSrc;
        private String role; // "DRIVER", "PASSENGER", etc.

        // Getters
        public long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAddress() { return address; }
        public String getEmail() { return email; }
        public String getImgSrc() { return imgSrc; }
        public String getRole() { return role; }

        public boolean isDriver() {
            return "DRIVER".equalsIgnoreCase(role);
        }
    }

    public static class VehicleInfo {
        private long id;
        private String type;
        private int numberOfSeats;
        private String model;
        private String licensePlate;
        private List<String> additionalServices;

        // Getters
        public long getId() { return id; }
        public String getType() { return type; }
        public int getNumberOfSeats() { return numberOfSeats; }
        public String getModel() { return model; }
        public String getLicensePlate() { return licensePlate; }
        public List<String> getAdditionalServices() { return additionalServices; }
    }

    public static class PendingChangeRequest {
        private long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String email;
        private String imgSrc;
        private String vehicleType;
        private int numberOfSeats;
        private String model;
        private String licensePlate;
        private List<String> additionalServices;

        // Getters
        public long getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAddress() { return address; }
        public String getEmail() { return email; }
        public String getImgSrc() { return imgSrc; }
        public String getVehicleType() { return vehicleType; }
        public int getNumberOfSeats() { return numberOfSeats; }
        public String getModel() { return model; }
        public String getLicensePlate() { return licensePlate; }
        public List<String> getAdditionalServices() { return additionalServices; }
    }

    // Main getters
    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public VehicleInfo getVehicleInfo() { return vehicleInfo; }
    public PendingChangeRequest getPendingChangeRequest() { return pendingChangeRequest; }

    public boolean hasPendingChanges() {
        return pendingChangeRequest != null;
    }

    public boolean isDriver() {
        return personalInfo != null && personalInfo.isDriver();
    }
}