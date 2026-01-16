package com.project.backend.models;

import com.project.backend.models.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.Set;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends AppUser{
    @ManyToMany
    Set<Route> favoriteRoutes;

    public Set<Route> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(Set<Route> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }
}

