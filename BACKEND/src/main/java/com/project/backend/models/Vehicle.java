package com.project.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String model;

    private String licensePlate;

    private Integer numberOfSeats;

    @OneToOne(fetch = FetchType.LAZY)
    private Driver driver;
    @ManyToOne(fetch = FetchType.EAGER)
    private VehicleType vehicleType;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AdditionalService> additionalServices;
}
