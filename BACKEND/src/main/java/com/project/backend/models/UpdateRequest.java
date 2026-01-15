package com.project.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UpdateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private String imgSrc;
    private String model;
    private String licensePlate;
    private int numberOfSeats;
    @ManyToOne
    private VehicleType vehicleType;
    @ManyToMany
    private Set<AdditionalService> additionalServices;
    @OneToOne
    private Driver driver;
}
