package com.project.backend.models;

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
}
