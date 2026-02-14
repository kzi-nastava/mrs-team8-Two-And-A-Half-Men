package com.project.backend.repositories.fixtures;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.Route;
import com.project.backend.models.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

public class RideRepositoryTestFindActiveRidesFixtures {
    // Test driver details
    public static final String DRIVER_EMAIL_1 = "driver1@example.com";
    public static final String DRIVER_EMAIL_2 = "driver2@example.com";
    public static final String DRIVER_EMAIL_3 = "driver3@example.com";


    public Driver createDriver(String firstName, String lastName, String email) {
        Driver driver = new Driver();
        driver.setId(null);
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setEmail(email);
        driver.setPassword("password123");
        driver.setPhoneNumber("+1234567890");
        driver.setAddress("123 Test Street");
        driver.setIsActive(Boolean.TRUE);
        driver.setIsBlocked(Boolean.FALSE);
        driver.setBlockReason(null);
        driver.setToken(null);
        driver.setTokenExpiration(null);
        driver.setImgSrc(null);
        return driver;
    }

    public Driver createDriver1() {
        return createDriver("John", "Driver", DRIVER_EMAIL_1);
    }

    public Driver createDriver2() {
        return createDriver("Jane", "Driver", DRIVER_EMAIL_2);
    }

    public Driver createDriver3() {
        return createDriver("Bob", "Driver", DRIVER_EMAIL_3);
    }

    public Ride createRide(Driver driver, RideStatus status) {
        Ride ride = new Ride();
        ride.setId(null);
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setCreatedAt(LocalDateTime.now());
        ride.setPrice(100.0);
        return ride;
    }


    public List<RideStatus> createActiveStatusesList() {
        return List.of(RideStatus.ACCEPTED, RideStatus.ACTIVE);
    }

    public List<RideStatus> createEmptyStatusesList() {
        return List.of();
    }


    public List<RideStatus> createSingleStatusList(RideStatus status) {
        return List.of(status);
    }


    public List<RideStatus> createCompletedStatusesList() {
        return List.of(RideStatus.FINISHED, RideStatus.CANCELLED);
    }



    public List<Driver> createDriversList(Driver... drivers) {
        return List.of(drivers);
    }

    public List<Driver> createEmptyDriversList() {
        return List.of();
    }
}
