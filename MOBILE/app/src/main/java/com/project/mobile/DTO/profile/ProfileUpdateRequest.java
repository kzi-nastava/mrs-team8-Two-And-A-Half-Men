package com.project.mobile.DTO.profile;

import java.util.List;

public class ProfileUpdateRequest {
    private PersonalData personalData;
    private VehicleData vehicleData;
    private String profileImageUrl;

    public ProfileUpdateRequest(PersonalData personalData, VehicleData vehicleData, String profileImageUrl) {
        this.personalData = personalData;
        this.vehicleData = vehicleData;
        this.profileImageUrl = profileImageUrl;
    }

    public static class PersonalData {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String email;

        public PersonalData(String firstName, String lastName, String phoneNumber, String address, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.email = email;
        }

        // Getters
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAddress() { return address; }
        public String getEmail() { return email; }
    }

    public static class VehicleData {
        private String type;
        private int numberOfSeats;
        private String model;
        private String licensePlate;
        private List<String> additionalServices;

        public VehicleData(String type, int numberOfSeats, String model, String licensePlate, List<String> additionalServices) {
            this.type = type;
            this.numberOfSeats = numberOfSeats;
            this.model = model;
            this.licensePlate = licensePlate;
            this.additionalServices = additionalServices;
        }

        // Getters
        public String getType() { return type; }
        public int getNumberOfSeats() { return numberOfSeats; }
        public String getModel() { return model; }
        public String getLicensePlate() { return licensePlate; }
        public List<String> getAdditionalServices() { return additionalServices; }
    }

    // Getters
    public PersonalData getPersonalData() { return personalData; }
    public VehicleData getVehicleData() { return vehicleData; }
    public String getProfileImageUrl() { return profileImageUrl; }
}