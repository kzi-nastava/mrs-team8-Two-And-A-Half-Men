package com.project.backend.services.fixtures;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;

public class RideServiceFinishRideTestsFixture {

    // ==================== Test Constants ====================
    public static final Long VALID_RIDE_ID = 1L;
    public static final Long NON_EXISTENT_RIDE_ID = 999L;
    public static final Long DRIVER_ID = 10L;
    public static final Long NEXT_RIDE_ID = 2L;

    // ==================== Helper Methods ====================

    public static Driver createDriver() {
        Driver driver = new Driver();
        driver.setId(DRIVER_ID);
        driver.setFirstName("John");
        driver.setLastName("Driver");
        driver.setEmail("driver@example.com");
        driver.setDriverStatus(DriverStatus.BUSY);
        driver.setIsBlocked(false);
        driver.setIsActive(true);
        return driver;
    }

    public static Ride createRide(Driver driver, RideStatus status) {
        Ride ride = new Ride();
        ride.setId(VALID_RIDE_ID);
        ride.setDriver(driver);
        ride.setStatus(status);
        return ride;
    }

    public static Ride createNextRide(Driver driver) {
        Ride nextRide = new Ride();
        nextRide.setId(NEXT_RIDE_ID);
        nextRide.setDriver(driver);
        nextRide.setStatus(RideStatus.ACCEPTED);
        return nextRide;
    }
}
