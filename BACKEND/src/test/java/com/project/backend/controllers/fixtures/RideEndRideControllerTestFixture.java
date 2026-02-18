package com.project.backend.controllers.fixtures;

import com.project.backend.DTO.Ride.CostTimeDTO;
import com.project.backend.models.Driver;
import com.project.backend.models.enums.DriverStatus;

public class RideEndRideControllerTestFixture {
    public Long DRIVER_ID = 10L;
    public Long UNAUTHORIZED_DRIVER_ID = 20L;

    public Long VALID_RIDE_ID = 1L;
    public Long NON_EXISTENT_RIDE_ID = 999L;
    public Long PENDING_RIDE_ID = 2L;
    public Long FINISHED_RIDE_ID = 3L;

    public final String DRIVER_EMAIL = "driver@example.com";
    public final String UNAUTHORIZED_DRIVER_EMAIL = "other.driver@example.com";

    public final Double TOTAL_COST = 1310.0;
    public final Double DURATION_MINUTES = 30.0;

    public final String ERROR_UNAUTHORIZED = "Unauthorized";
    public final String ERROR_RIDE_NOT_FOUND = "Ride with id " + NON_EXISTENT_RIDE_ID + " not found";
    public final String ERROR_DRIVER_NOT_AUTHORIZED = "Driver not authorized to end this ride";
    public final String ERROR_RIDE_NOT_ACTIVE = "Ride is not active";




    public Driver createValidDriver() {
        Driver driver = new Driver();
        driver.setId(DRIVER_ID);
        driver.setFirstName("John");
        driver.setLastName("Driver");
        driver.setEmail(DRIVER_EMAIL);
        driver.setPassword("password123");
        driver.setPhoneNumber("+1234567890");
        driver.setIsActive(true);
        driver.setIsBlocked(false);
        driver.setDriverStatus(DriverStatus.BUSY);
        return driver;
    }

    public Driver createUnauthorizedDriver() {
        Driver driver = new Driver();
        driver.setId(UNAUTHORIZED_DRIVER_ID);
        driver.setFirstName("Other");
        driver.setLastName("Driver");
        driver.setEmail(UNAUTHORIZED_DRIVER_EMAIL);
        driver.setPassword("password123");
        driver.setPhoneNumber("+0987654321");
        driver.setIsActive(true);
        driver.setIsBlocked(false);
        driver.setDriverStatus(DriverStatus.BUSY);
        return driver;
    }



    public CostTimeDTO createSuccessfulEndRideResponse() {
        CostTimeDTO costTimeDTO = new CostTimeDTO();
        costTimeDTO.setCost(TOTAL_COST);
        costTimeDTO.setTime(DURATION_MINUTES);
        return costTimeDTO;
    }

    public CostTimeDTO createEndRideResponseWithZeroTime() {
        CostTimeDTO costTimeDTO = new CostTimeDTO();
        costTimeDTO.setCost(TOTAL_COST);
        costTimeDTO.setTime(0.0);
        return costTimeDTO;
    }
}
