package com.project.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.HashSet;

@Entity
public class UpdateRequest {
    @Id
    private Long id;


    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;
    private String imgSrc;
    private String model;
    private String licensePlate;
    private int numberOfSeats;
    @ManyToOne
    private VehicleType vehicleType;
    @ManyToMany
    private HashSet<AdditionalService> additionalServices;
}
