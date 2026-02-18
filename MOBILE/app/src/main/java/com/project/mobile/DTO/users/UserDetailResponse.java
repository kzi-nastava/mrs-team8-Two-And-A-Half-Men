package com.project.mobile.DTO.users;

import com.project.mobile.DTO.profile.PendingChangeRequest;
import com.project.mobile.DTO.profile.PersonalInfo;
import com.project.mobile.DTO.profile.VehicleInfo;

public class UserDetailResponse {
    private PersonalInfo personalInfo;
    private VehicleInfo vehicleInfo;
    private PendingChangeRequest pendingChangeRequest;

    public UserDetailResponse() {}

    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }

    public VehicleInfo getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(VehicleInfo vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public PendingChangeRequest getPendingChangeRequest() { return pendingChangeRequest; }
    public void setPendingChangeRequest(PendingChangeRequest pendingChangeRequest) {
        this.pendingChangeRequest = pendingChangeRequest;
    }
}
