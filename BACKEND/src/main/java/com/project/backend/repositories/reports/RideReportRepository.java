package com.project.backend.repositories.reports;

import com.project.backend.models.Ride;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RideReportRepository {
    // Get rides for a specific driver within date range
    @Query("SELECT r FROM Ride r " +
            "WHERE r.driver IS NOT NULL " +
            "AND r.driver.id = :driverId " +
            "AND r.status = 'FINISHED' " +
            "AND r.endTime >= :startDate " +
            "AND r.endTime < :endDate " +
            "ORDER BY r.endTime")
    List<Ride> findCompletedRidesByDriverAndDateRange(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get rides for a specific passenger within date range
    @Query("SELECT r FROM Ride r " +
            "JOIN r.passengers p " +
            "WHERE p.user IS NOT NULL " +
            "AND p.user.id = :passengerId " +
            "AND r.status = 'FINISHED' " +
            "AND r.endTime >= :startDate " +
            "AND r.endTime < :endDate " +
            "ORDER BY r.endTime")
    List<Ride> findCompletedRidesByPassengerAndDateRange(
            @Param("passengerId") Long passengerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get all completed rides for all drivers within date range
    @Query("SELECT r FROM Ride r " +
            "WHERE r.status = 'FINISHED' " +
            "AND r.driver IS NOT NULL " +
            "AND r.endTime >= :startDate " +
            "AND r.endTime < :endDate " +
            "ORDER BY r.endTime")
    List<Ride> findAllCompletedRidesByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get all drivers who had rides in date range
    @Query("SELECT DISTINCT r.driver.id FROM Ride r " +
            "WHERE r.driver IS NOT NULL " +
            "AND r.status = 'FINISHED' " +
            "AND r.endTime >= :startDate " +
            "AND r.endTime < :endDate")
    List<Long> findDriverIdsWithRidesInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get all passengers who had rides in date range
    @Query("SELECT DISTINCT p.user.id FROM Ride r " +
            "JOIN r.passengers p " +
            "WHERE p.user IS NOT NULL " +
            "AND r.status = 'FINISHED' " +
            "AND r.endTime >= :startDate " +
            "AND r.endTime < :endDate")
    List<Long> findPassengerIdsWithRidesInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
