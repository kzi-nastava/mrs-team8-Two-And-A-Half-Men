package com.project.backend.DTO.users;

import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isBlocked;
    private UserRole role;
    private DriverStatus driverStatus;
    private Boolean hasPendingRequests;
}