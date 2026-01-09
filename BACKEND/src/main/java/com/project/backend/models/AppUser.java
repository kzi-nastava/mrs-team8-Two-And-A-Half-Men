package com.project.backend.models;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;


@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;
    private String imgSrc;
    private String token;
    private LocalDateTime tokenExpiration;
    private Boolean isActive;
    private Boolean isBLocked;
    @OneToMany
    HashSet<Notification> notifications;
    @OneToMany
    List<Message> messages;



}
