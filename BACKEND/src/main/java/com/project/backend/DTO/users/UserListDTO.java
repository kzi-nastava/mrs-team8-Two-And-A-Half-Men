package com.project.backend.DTO.users;

import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.models.UpdateRequest;
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
public class UserListDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private Boolean isBlocked;
    private DriverStatus driverStatus; // Only for drivers

    // Flags
    private Boolean hasPendingRequests;

    public UserListDTO(AppUser user, UpdateRequest updateRequest) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.isBlocked = user.getIsBlocked();
        if (this.role == UserRole.DRIVER) {
            this.driverStatus = ((Driver) user).getDriverStatus();
            this.hasPendingRequests = (updateRequest != null);
        } else {
            this.driverStatus = null;
            this.hasPendingRequests = null;
        }
    }
}
