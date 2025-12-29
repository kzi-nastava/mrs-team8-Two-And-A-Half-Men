package com.project.backend.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends AppUser{
    @ManyToMany
    HashSet<Route> favoriteRoutes;
}
