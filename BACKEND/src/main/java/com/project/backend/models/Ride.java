package com.project.backend.models;

import com.project.backend.models.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private String path;

    private String cancellationReason;

    private Double totalCost;

    @ManyToOne(fetch = FetchType.EAGER)
    private Driver driver;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer rideOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    private VehicleType vehicleType;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<AdditionalService> additionalServices;

    @OneToMany(fetch = FetchType.LAZY)
    List<Passenger> passengers;

}
