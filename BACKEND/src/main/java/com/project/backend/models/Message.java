package com.project.backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    private String text;
    private LocalDateTime timestamp;
    private boolean IsAdminRead;
    private boolean IsUserRead;
    private boolean IsAdmin;


}
