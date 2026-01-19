package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

public interface RideRepository extends JpaRepository<Ride, Long> {
    @Query("SELECT r FROM Ride r WHERE r.driver = :driver " +
            "AND r.status IN :statuses")
    Optional<Ride> findRideOfDriverWithStatus(@Param("driver") Driver driver, @Param("statuses") List<RideStatus> status);
}
