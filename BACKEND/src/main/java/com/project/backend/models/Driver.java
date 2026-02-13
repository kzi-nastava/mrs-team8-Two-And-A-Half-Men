package com.project.backend.models;

import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public Long calculateActivityRange(LocalDateTime rangeStartTime, LocalDateTime rangeEndTime, LocalDateTime referenceTime) {
        return driverActivities.stream()
                .mapToLong(a -> {
                    // Replace null endTime with referenceTime
                    LocalDateTime activityEnd =
                            a.getEndTime() != null ? a.getEndTime() : referenceTime;

                    // Clamp interval to query window
                    LocalDateTime start =
                            a.getStartTime().isAfter(rangeStartTime) ? a.getStartTime() : rangeStartTime;

                    LocalDateTime end =
                            activityEnd.isBefore(rangeEndTime) ? activityEnd : rangeEndTime;

                    if (!start.isBefore(end)) {
                        return 0;
                    }

                    return Duration.between(start, end).toMinutes();
                })
                .sum();
    }
}
