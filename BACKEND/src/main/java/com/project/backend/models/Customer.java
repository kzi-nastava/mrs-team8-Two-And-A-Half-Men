package com.project.backend.models;

import com.project.backend.models.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@DiscriminatorValue("CUSTOMER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends AppUser{
    @ManyToMany
    Set<Route> favoriteRoutes;

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }
}

