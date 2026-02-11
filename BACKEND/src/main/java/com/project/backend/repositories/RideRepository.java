package com.project.backend.repositories;

import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.reports.RideReportRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long>, RideReportRepository, JpaSpecificationExecutor<Ride> {
    
    @Query("SELECT r FROM Ride r WHERE r.driver = :driver " +
            "AND r.status IN :statuses")
    Optional<Ride> findRideOfDriverWithStatus(@Param("driver") Driver driver, @Param("statuses") List<RideStatus> status);


    Optional<Ride> findFirstByDriverAndStatusIn(Driver driver, List<RideStatus> statuses);

    Optional<Ride> findFirstByRideOwnerAndStatusIn(AppUser customer, List<RideStatus> statuses);

    List<Ride> findByDriverIdInAndEndTimeIsNullOrderByCreatedAtAsc(Collection<Long> driversIds);

    List<Ride> findByRideOwner(AppUser customer);

    @Query("""
        SELECT r FROM Ride r
        WHERE r.status IN :statuses AND r.driver IN :drivers
        ORDER BY r.createdAt DESC
        """)
    List<Ride> findActiveRides(
            @Param("statuses") List<RideStatus> statuses,
            @Param("drivers") List<Driver> drivers
    );

    @Query("""
            SELECT r FROM Ride r
            WHERE r.scheduledTime IS NOT NULL
                AND r.scheduledTime > CURRENT_TIMESTAMP
                AND r.status IN('PENDING', 'ACCEPTED')
        """)
    Iterable<Ride> findFutureScheduledRides();

}
