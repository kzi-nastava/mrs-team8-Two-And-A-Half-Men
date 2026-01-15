package com.project.backend.DTO.Profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProfileDTO {
    private PersonalInfoDTO personalInfo;
    private VehicleInfoDTO vehicleInfo;
    private List<VehicleTypeDTO> vehicleTypes;
    private List<AdditionalServiceDTO> additionalServices;
    private ChangeRequestDTO pendingChangeRequest;
}
