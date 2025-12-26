package com.project.backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Entity
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime scheduledTime;
    private String status;
    private String path;
    private String cancellationReason;
    private double totalCost;
    @ManyToOne
    private Driver driver;
    @ManyToOne
    private Customer rideowner;
    @ManyToOne
    private VehicleType vehicleType;
    @ManyToMany
    HashSet<AdditionalService> additionalServices;
    @OneToMany
    List<Passanger> passangers;

}
