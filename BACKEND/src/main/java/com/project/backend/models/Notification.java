package com.project.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    private String data; // Optional field for additional data (e.g., ride details, driver info)
}
