package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByDriverId(Long driverId);
    Optional<Vehicle> findByDriver(Driver driver);

    List<Vehicle> findByDriverIdIn(Collection<Long> driversIds);
}
