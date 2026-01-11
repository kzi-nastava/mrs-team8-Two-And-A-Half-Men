package com.project.backend.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("DRIVER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends AppUser {

    private String driverState;

    @OneToMany(fetch = FetchType.LAZY)
    private List<DriverActivity> driverActivities;
}
