package com.project.backend.services.fixtures;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;

import java.time.LocalDateTime;

public class RideEndingRideServiceTestsFixture {
    public final Long VALID_RIDE_ID = 1L;
    public final Long NON_EXISTENT_RIDE_ID = 999L;

    public final Long VALID_DRIVER_ID = 10L;
    public final Long UNAUTHORIZED_DRIVER_ID = 20L;

    public final Double RIDE_PRICE = 50.0;
    public final Double RIDE_DISTANCE = 10.5;
    public final Double CALCULATED_TOTAL_COST = RIDE_PRICE + 120 * RIDE_DISTANCE;

    public final String FINISHED_PATH = "u4pruydqqvj8,u4pruydqqvj9,u4pruydqqvja";

    public final LocalDateTime startTime = LocalDateTime.of(2026, 5, 31, 10, 0);
    public final LocalDateTime endTime = LocalDateTime.of(2026, 5, 31, 10, 30);
    public final long EXPECTED_DURATION_MINUTES = 30L;



    public Driver createValidDriver() {
        Driver driver = new Driver();
        driver.setId(VALID_DRIVER_ID);
        driver.setFirstName("John");
        driver.setLastName("Driver");
        driver.setEmail("driver@example.com");
        driver.setIsBlocked(false);
        driver.setIsActive(true);
        driver.setDriverStatus(DriverStatus.BUSY);
        return driver;
    }

    public Driver createUnauthorizedDriver() {
        Driver driver = new Driver();
        driver.setId(UNAUTHORIZED_DRIVER_ID);
        driver.setFirstName("Other");
        driver.setLastName("Driver");
        driver.setEmail("other.driver@example.com");
        driver.setIsBlocked(false);
        driver.setIsActive(true);
        driver.setDriverStatus(DriverStatus.BUSY);
        return driver;
    }


    public Ride createActiveRide() {
        Ride ride = new Ride();
        ride.setId(VALID_RIDE_ID);
        ride.setDriver(createValidDriver());
        ride.setStatus(RideStatus.ACTIVE);
        ride.setPrice(RIDE_PRICE);
        ride.setStartTime(startTime);
        return ride;
    }

    public Ride createActiveRideWithNullPrice() {
        Ride ride = new Ride();
        ride.setId(VALID_RIDE_ID);
        ride.setDriver(createValidDriver());
        ride.setStatus(RideStatus.ACTIVE);
        ride.setPrice(null);
        ride.setStartTime(startTime);
        return ride;
    }

    public Ride createActiveRideWithNullTimes() {
        Ride ride = new Ride();
        ride.setId(VALID_RIDE_ID);
        ride.setDriver(createValidDriver());
        ride.setStatus(RideStatus.ACTIVE);
        ride.setPrice(RIDE_PRICE);
        ride.setStartTime(null);
        return ride;
    }

    public Ride createPendingRide() {
        Ride ride = new Ride();
        ride.setId(VALID_RIDE_ID);
        ride.setDriver(createValidDriver());
        ride.setStatus(RideStatus.PENDING);
        ride.setPrice(RIDE_PRICE);
        return ride;
    }

    public Ride createAcceptedRide() {
        Ride ride = new Ride();
        ride.setId(VALID_RIDE_ID);
        ride.setDriver(createValidDriver());
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setPrice(RIDE_PRICE);
        return ride;
    }

}
