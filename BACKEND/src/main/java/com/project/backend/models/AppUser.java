package com.project.backend.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(fetch = FetchType.LAZY)
    Set<Notification> notifications;

    @OneToMany(fetch = FetchType.LAZY)
    List<Message> messages;
}
