package com.project.backend.DTO.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileResponseDTO {
    String accessToken;
    ProfileDTO profile;
}
