package com.project.backend.models;

import com.project.backend.models.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser implements UserDetails{

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
    private Boolean isBlocked;
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    Set<Notification> notifications;
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    List<Message> messages;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String className = this.getClass().getSimpleName();
        return List.of(new SimpleGrantedAuthority("ROLE_" + className.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    public UserRole getRole() {
        return null;
    }

    public String firstNameAndLastName() {
        return this.firstName + " " + this.lastName;
    }
}
