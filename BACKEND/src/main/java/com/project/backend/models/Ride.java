package com.project.backend.models;

import com.project.backend.models.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime scheduledTime;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(columnDefinition = "text")
    private String path;

    private Double distanceKm;

    private String cancellationReason;

    private Double price;

    private Double totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer rideOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    private VehicleType vehicleType;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<AdditionalService> additionalServices;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ride")
    List<Passenger> passengers;

    @ManyToOne(fetch = FetchType.EAGER)
    private Route route;
}
