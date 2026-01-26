package com.project.backend.repositories;

import com.project.backend.models.Customer;
import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByAccessToken(String accessToken);
    @Query("SELECT p FROM Passenger p WHERE p.user = :customer " +
            "AND p.ride IS NOT NULL " +
            "AND p.ride.status IN :statuses")
    Optional<Passenger> findByCustomerWithRideStatus(@Param("customer") Customer customer ,
                                                             @Param("statuses") Iterable<RideStatus> statuses);

    Optional<Passenger> findByUserId(Long id);

    Optional<Passenger> findByUserAndRide(Customer user, Ride ride);
}
