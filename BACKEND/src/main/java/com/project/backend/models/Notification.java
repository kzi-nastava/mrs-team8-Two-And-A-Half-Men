package com.project.backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    private LocalDateTime timestamp;
    private String title;
    private String message;
    private boolean read;

   // Sta nam je i zasto je JSON ovde ?
}
