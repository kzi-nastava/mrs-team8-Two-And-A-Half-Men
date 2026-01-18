package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    Page<Ride> findByDriver(Driver driver, Pageable pageable);

    @Query("""
            SELECT r FROM Ride r
            WHERE r.driver = :driver
            AND r.startTime >= :startDate
            AND r.startTime <= :endDate
            """)
    Page<Ride> findByDriverAndDateRange(
            Driver driver,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM Ride r
            WHERE r.driver = :driver
            AND r.startTime >= :startDate
            """)
    Page<Ride> findByDriverAndStartDateAfter(
            Driver driver,
            LocalDateTime startDate,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM Ride r
            WHERE r.driver = :driver
            AND r.startTime <= :endDate
            """)
    Page<Ride> findByDriverAndEndDateBefore(
            Driver driver,
            LocalDateTime endDate,
            Pageable pageable
    );
}
