package com.project.mobile.DTO.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
    private PersonalInfo personalInfo;
    private VehicleInfo vehicleInfo;
    private PendingChangeRequest pendingChangeRequest;
    private String accessToken;

    public boolean isDriver() {
        return personalInfo != null && personalInfo.isDriver();
    }
}
