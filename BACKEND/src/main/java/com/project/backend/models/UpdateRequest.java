package com.project.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @OneToOne
    private Driver driver;
}
