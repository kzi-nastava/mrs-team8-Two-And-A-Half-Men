package com.project.backend.DTO.Profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {
    private PersonalInfoDTO personalInfo;
    private VehicleInfoDTO vehicleInfo;
    private ChangeRequestDTO pendingChangeRequest;
}
