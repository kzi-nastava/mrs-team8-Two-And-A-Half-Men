package com.project.backend.models;

import com.project.backend.models.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
@DiscriminatorValue("DRIVER")
public class Driver extends AppUser {
    private String driverState;
    @OneToMany
    private List<DriverActivity> driverActivities;

    @Override
    public UserRole getRole() {
        return UserRole.DRIVER;
    }
}
