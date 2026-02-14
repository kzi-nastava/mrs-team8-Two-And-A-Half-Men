package com.project.backend.repositories.fixtures;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.Route;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;

import java.time.LocalDateTime;

/**
 * Fixture class for RideRepository DataJpa tests.
 * Provides factory methods for creating test entities for repository method testing.
 */
public class RideRepositoryFinishRideTestsFixture {

    // Test timestamps
    public static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 2, 14, 10, 0);
    public static final LocalDateTime EARLIER_TIME = BASE_TIME.minusHours(2);
    public static final LocalDateTime LATER_TIME = BASE_TIME.plusHours(2);
    public static final LocalDateTime EARLIEST_TIME = BASE_TIME.minusHours(5);

    // ==================== Driver Factory Methods ====================

    /**
     * Creates a driver with specified details
     */
    public static Driver createDriver(String email, String firstName, String lastName) {
        Driver driver = new Driver();
        driver.setId(null); // Will be auto-generated
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setEmail(email);
        driver.setPassword("password123");
        driver.setPhoneNumber("+1234567890");
        driver.setAddress("123 Driver Street");
        driver.setIsActive(Boolean.TRUE);
        driver.setIsBlocked(Boolean.FALSE);
        driver.setDriverStatus(DriverStatus.ACTIVE);
        return driver;
    }

    public static Driver createDriver1() {
        return createDriver("driver1@example.com", "John", "Driver");
    }

    public static Driver createDriver2() {
        return createDriver("driver2@example.com", "Jane", "Driver");
    }

    // ==================== Route Factory Methods ====================

    /**
     * Creates a basic route
     */
    public static Route createRoute() {
        Route route = new Route();
        route.setId(null); // Will be auto-generated
        route.setGeoHash("u4pruydqqvj");
        return route;
    }

    // ==================== Ride Factory Methods ====================

    /**
     * Creates a ride with specified driver, status, and creation time
     */
    public static Ride createRide(Driver driver, Route route, RideStatus status, LocalDateTime createdAt) {
        Ride ride = new Ride();
        ride.setId(null); // Will be auto-generated
        ride.setDriver(driver);
        ride.setRoute(route);
        ride.setStatus(status);
        ride.setCreatedAt(createdAt);
        ride.setPrice(100.0);
        ride.setDistanceKm(10.0);
        return ride;
    }

    /**
     * Creates an ACCEPTED ride
     */
    public static Ride createAcceptedRide(Driver driver, Route route, LocalDateTime createdAt) {
        return createRide(driver, route, RideStatus.ACCEPTED, createdAt);
    }

    /**
     * Creates a PENDING ride
     */
    public static Ride createPendingRide(Driver driver, Route route, LocalDateTime createdAt) {
        return createRide(driver, route, RideStatus.PENDING, createdAt);
    }

    /**
     * Creates a FINISHED ride
     */
    public static Ride createFinishedRide(Driver driver, Route route, LocalDateTime createdAt) {
        return createRide(driver, route, RideStatus.FINISHED, createdAt);
    }

    /**
     * Creates an ACTIVE ride
     */
    public static Ride createActiveRide(Driver driver, Route route, LocalDateTime createdAt) {
        return createRide(driver, route, RideStatus.ACTIVE, createdAt);
    }

    /**
     * Creates an INTERRUPTED ride
     */
    public static Ride createInterruptedRide(Driver driver, Route route, LocalDateTime createdAt) {
        return createRide(driver, route, RideStatus.INTERRUPTED, createdAt);
    }
}