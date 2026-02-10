package com.project.backend.repositories;

import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.reports.RideReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long>, RideReportRepository, JpaSpecificationExecutor<Ride> {

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

    Optional<Ride> findFirstByRideOwnerAndStatusIn(Customer customer, List<RideStatus> statuses);

    List<Ride> findByDriverIdInAndEndTimeIsNullOrderByCreatedAtAsc(Collection<Long> driversIds);
    Optional<Ride> findById(Long rideId);

    List<Ride> findByRideOwner(Customer customer);

    @Query("""
        SELECT r FROM Ride r
        WHERE r.status IN :statuses
          AND (:firstName IS NULL OR LOWER(r.driver.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
          AND (:lastName IS NULL OR LOWER(r.driver.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
        ORDER BY r.createdAt DESC
        """)
    Page<Ride> findActiveRides(
            @Param("statuses") List<RideStatus> statuses,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable
    );

    @Query("""
    SELECT DISTINCT r
    FROM Ride r
    JOIN r.passengers p
    WHERE p.user = :customer
                                  AND coalesce(r.startTime, '1970-01-01 00:00:00') >= COALESCE(:startTime, coalesce(r.startTime, '1970-01-01 00:00:00'))
                                  AND coalesce(r.startTime , '1970-01-01 00:00:00') <= COALESCE(:endTime, coalesce(r.startTime , '1970-01-01 00:00:00') )
      AND (r.status IN :statuses)
""")
    Page<Ride> findRidesByPassengerCustomerWithFilters(
            @Param("customer") Customer customer,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<RideStatus> statuses,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM Ride r
            WHERE r.scheduledTime IS NOT NULL
                AND r.scheduledTime > CURRENT_TIMESTAMP
                AND r.status IN('PENDING', 'ACCEPTED')
        """)
    Iterable<Ride> findFutureScheduledRides();
}
