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
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<DriverActivity> driverActivities;

    @OneToOne(mappedBy = "driver", fetch = FetchType.EAGER)
    private UpdateRequest updateRequest;

    @Override
    public UserRole getRole() {
        return UserRole.DRIVER;
    }
}
