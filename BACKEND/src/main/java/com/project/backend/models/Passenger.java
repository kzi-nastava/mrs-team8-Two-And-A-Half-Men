package com.project.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    private Long id;

    private String email;

    private String accessToken;

    @ManyToOne(fetch = FetchType.EAGER)
    private AppUser user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Ride ride;

    private String inconsistencyNote;

    private Integer driverRating;

    private Integer vehicleRating;

    private String comment;
}
