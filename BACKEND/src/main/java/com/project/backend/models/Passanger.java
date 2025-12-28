package com.project.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Passanger {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    private Long id;

    private String email;
    private String accessToken;
    @ManyToOne(optional = true)
    private Customer user;
    private String note;
    private int driverRating;
    private int vehicleRating;
    private String comment;
}
