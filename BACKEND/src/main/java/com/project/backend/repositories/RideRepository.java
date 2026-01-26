package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    @Query("SELECT r FROM Ride r WHERE r.driver = :driver " +
            "AND r.status IN :statuses")
    Optional<Ride> findRideOfDriverWithStatus(@Param("driver") Driver driver, @Param("statuses") List<RideStatus> status);


    Optional<Ride> findFirstByDriverAndStatusIn(Driver driver, List<RideStatus> statuses);

    List<Ride> findByDriverIdInAndEndTimeIsNullOrderByCreatedAtAsc(Collection<Long> driversIds);
    Optional<Ride> findById(Long rideId);
}
