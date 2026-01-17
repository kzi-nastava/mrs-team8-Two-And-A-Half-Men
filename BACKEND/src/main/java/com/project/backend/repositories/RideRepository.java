package com.project.backend.repositories;

import com.project.backend.models.Customer;
import com.project.backend.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findById(Long rideId);
}
