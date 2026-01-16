package com.project.backend.models;

import com.project.backend.models.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends AppUser{
    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }
}
