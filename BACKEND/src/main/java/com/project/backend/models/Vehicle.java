package com.project.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String model;

    private String licensePlate;

    private Integer numberOfSeats;

    @OneToOne(fetch = FetchType.LAZY)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    private VehicleType vehicleType;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<AdditionalService> additionalServices;
}
