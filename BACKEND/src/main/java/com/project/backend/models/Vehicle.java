package com.project.backend.models;

import jakarta.persistence.*;

import java.util.HashSet;

@Entity
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String model;
    private String licensePlate;
    private int numberOfSeats;
    @OneToOne
    private Driver driver;
    @ManyToOne
    private VehicleType vehicleType;
    @ManyToMany
    private HashSet<AdditionalService> additionalServices;
}
