package com.project.backend.models;

import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("DRIVER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends AppUser {
    @Enumerated(EnumType.STRING)
    private DriverStatus driverStatus;
    @OneToMany
    private List<DriverActivity> driverActivities;

    @Override
    public UserRole getRole() {
        return UserRole.DRIVER;
    }
}
