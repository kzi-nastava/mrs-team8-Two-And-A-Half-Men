package com.project.mobile.DTO.users;

public class DriverRegistrationRequest {
    private DriverRegistrationRequestPersonal personalInfo;
    private DriverRegistrationRequestVehicle vehicleInfo;

    public DriverRegistrationRequest() {}

    public DriverRegistrationRequestPersonal getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(DriverRegistrationRequestPersonal personalInfo) {
        this.personalInfo = personalInfo;
    }

    public DriverRegistrationRequestVehicle getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(DriverRegistrationRequestVehicle vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }
}
