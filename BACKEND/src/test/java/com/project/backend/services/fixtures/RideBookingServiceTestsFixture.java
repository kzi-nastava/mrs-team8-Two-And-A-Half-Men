package com.project.backend.services.fixtures;

import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Route.RouteItemDTO;
import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.models.*;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fixture class for RideBookingService tests.
 * Holds all test data, constants, and factory methods for creating test objects.
 */
@Getter
public class RideBookingServiceTestsFixture {

    // User IDs
    public static final Long VALID_USER_ID = 1L;
    public static final Long NON_EXISTENT_USER_ID = 999L;
    public static final Long BLOCKED_USER_ID = 2L;

    // Vehicle Type IDs
    public static final Long VALID_VEHICLE_TYPE_ID = 1L;
    public static final Long NON_EXISTENT_VEHICLE_TYPE_ID = 999L;

    // Additional Service IDs
    public static final Long ADDITIONAL_SERVICE_ID_1 = 1L;
    public static final Long ADDITIONAL_SERVICE_ID_2 = 2L;
    public static final Long NON_EXISTENT_ADDITIONAL_SERVICE_ID = 999L;

    // Route IDs
    public static final Long VALID_ROUTE_ID = 1L;
    public static final Long NON_EXISTENT_ROUTE_ID = 999L;

    // Ride ID
    public static final Long SAVED_RIDE_ID = 100L;

    // Email addresses
    public static final String EXISTING_USER_EMAIL_1 = "existing1@example.com";
    public static final String EXISTING_USER_EMAIL_2 = "existing2@example.com";
    public static final String NON_EXISTING_USER_EMAIL_1 = "nonexisting1@example.com";
    public static final String NON_EXISTING_USER_EMAIL_2 = "nonexisting2@example.com";
    public static final String RIDE_OWNER_EMAIL = "owner@example.com";

    // Prices
    public static final Double VEHICLE_TYPE_PRICE = 15.0;

    // Distance
    public static final Double ESTIMATED_DISTANCE = 5.5;

    // Block reason
    public static final String BLOCK_REASON = "Violated terms of service";

    // Time reference
    public static final LocalDateTime currentTime = LocalDateTime.of(2026, 5, 31, 12, 0);

    public RideBookingServiceTestsFixture() {
    }

    // ==================== AppUser Factory Methods ====================

    public AppUser createValidRideOwner() {
        return AppUser.builder()
                .id(VALID_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .email(RIDE_OWNER_EMAIL)
                .isBlocked(false)
                .isActive(true)
                .build();
    }

    public AppUser createBlockedRideOwner() {
        return AppUser.builder()
                .id(BLOCKED_USER_ID)
                .firstName("Blocked")
                .lastName("User")
                .email("blocked@example.com")
                .isBlocked(true)
                .blockReason(BLOCK_REASON)
                .isActive(true)
                .build();
    }

    public AppUser createExistingUser1() {
        return AppUser.builder()
                .id(10L)
                .firstName("Alice")
                .lastName("Smith")
                .email(EXISTING_USER_EMAIL_1)
                .isBlocked(false)
                .isActive(true)
                .build();
    }

    public AppUser createExistingUser2() {
        return AppUser.builder()
                .id(11L)
                .firstName("Bob")
                .lastName("Johnson")
                .email(EXISTING_USER_EMAIL_2)
                .isBlocked(false)
                .isActive(true)
                .build();
    }

    // ==================== VehicleType Factory Methods ====================

    public VehicleType createValidVehicleType() {
        return VehicleType.builder()
                .id(VALID_VEHICLE_TYPE_ID)
                .typeName("Sedan")
                .description("Standard sedan vehicle")
                .price(VEHICLE_TYPE_PRICE)
                .build();
    }

    // ==================== AdditionalService Factory Methods ====================

    public AdditionalService createAdditionalService1() {
        return AdditionalService.builder()
                .id(ADDITIONAL_SERVICE_ID_1)
                .name("Pet Friendly")
                .description("Vehicle allows pets")
                .build();
    }

    public AdditionalService createAdditionalService2() {
        return AdditionalService.builder()
                .id(ADDITIONAL_SERVICE_ID_2)
                .name("Baby Seat")
                .description("Vehicle has baby seat")
                .build();
    }

    public List<AdditionalService> createAllAdditionalServices() {
        return List.of(createAdditionalService1(), createAdditionalService2());
    }

    public List<AdditionalService> createPartialAdditionalServices() {
        return List.of(createAdditionalService1()); // Only one service found
    }

    // ==================== Route Factory Methods ====================

    public Route createValidRoute() {
        return Route.builder()
                .id(VALID_ROUTE_ID)
                .geoHash("u4pruydqqvj")
                .build();
    }

    public Route createNewRoute() {
        return Route.builder()
                .id(50L)
                .geoHash("u4pruydqqvj")
                .build();
    }

    public List<RouteItemDTO> createRouteItems() {
        List<RouteItemDTO> items = new ArrayList<>();
        items.add(new RouteItemDTO("123 Main St", 40.7128, -74.0060));
        items.add(new RouteItemDTO("456 Elm St", 40.7580, -73.9855));
        return items;
    }

    // ==================== Driver Factory Methods ====================

    public Driver createValidDriver() {
        Driver driver = new Driver();
        driver.setId(20L);
        driver.setFirstName("Driver");
        driver.setLastName("Smith");
        driver.setEmail("driver@example.com");
        driver.setIsBlocked(false);
        driver.setIsActive(true);
        driver.setDriverStatus(DriverStatus.ACTIVE);
        return driver;
    }

    public FindDriverDTO createFindDriverDTO() {
        return FindDriverDTO.builder()
                .driver(createValidDriver())
                .estimatedDistance(ESTIMATED_DISTANCE)
                .build();
    }

    // ==================== RideBookingParametersDTO Factory Methods ====================

    public RideBookingParametersDTO createMinimalValidRequest() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .build();
    }

    public RideBookingParametersDTO createRequestWithValidVehicleType() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .build();
    }

    public RideBookingParametersDTO createRequestWithNonExistentVehicleType() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(NON_EXISTENT_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .build();
    }

    public RideBookingParametersDTO createRequestWithoutAdditionalServices() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .additionalServicesIds(null)
                .build();
    }

    public RideBookingParametersDTO createRequestWithEmptyAdditionalServices() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .additionalServicesIds(new ArrayList<>())
                .build();
    }

    public RideBookingParametersDTO createRequestWithValidAdditionalServices() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .additionalServicesIds(List.of(ADDITIONAL_SERVICE_ID_1, ADDITIONAL_SERVICE_ID_2))
                .build();
    }

    public RideBookingParametersDTO createRequestWithSomeNonExistentAdditionalServices() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .additionalServicesIds(List.of(ADDITIONAL_SERVICE_ID_1, NON_EXISTENT_ADDITIONAL_SERVICE_ID))
                .build();
    }

    public RideBookingParametersDTO createRequestWithExistingRouteId() {
        return RideBookingParametersDTO.builder()
                .routeId(VALID_ROUTE_ID)
                .build();
    }

    public RideBookingParametersDTO createRequestWithNonExistentRouteId() {
        return RideBookingParametersDTO.builder()
                .routeId(NON_EXISTENT_ROUTE_ID)
                .build();
    }

    public RideBookingParametersDTO createRequestWithRouteObject() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .build();
    }

    public RideBookingParametersDTO createRequestWithoutPassengers() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .passengers(null)
                .build();
    }

    public RideBookingParametersDTO createRequestWithExistingUserPassengers() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .passengers(List.of(EXISTING_USER_EMAIL_1, EXISTING_USER_EMAIL_2))
                .build();
    }

    public RideBookingParametersDTO createRequestWithNonExistingUserPassengers() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .passengers(List.of(NON_EXISTING_USER_EMAIL_1, NON_EXISTING_USER_EMAIL_2))
                .build();
    }

    public RideBookingParametersDTO createRequestWithMixedPassengers() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .passengers(List.of(EXISTING_USER_EMAIL_1, NON_EXISTING_USER_EMAIL_1))
                .build();
    }

    public RideBookingParametersDTO createRequestWithValidScheduledTime() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .scheduledTime(currentTime.plusHours(2))
                .build();
    }

    public RideBookingParametersDTO createRequestWithPastScheduledTime() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .scheduledTime(currentTime.minusHours(1))
                .build();
    }

    public RideBookingParametersDTO createRequestWithTooFarFutureScheduledTime(int maxHours) {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .scheduledTime(currentTime.plusHours(maxHours + 1))
                .build();
    }

    public RideBookingParametersDTO createRequestWithoutScheduledTime() {
        return RideBookingParametersDTO.builder()
                .route(createRouteItems())
                .build();
    }

    /**
     * Creates a scheduled ride with a driver already assigned
     */
    public Ride createScheduledRideWithDriver() {
        return Ride.builder()
                .id(SAVED_RIDE_ID)
                .status(RideStatus.PENDING)
                .scheduledTime(currentTime.plusHours(2))
                .createdAt(currentTime)
                .driver(createValidDriver())
                .rideOwner(createValidRideOwner())
                .vehicleType(createValidVehicleType())
                .route(createValidRoute())
                .passengers(List.of())
                .additionalServices(Set.of())
                .build();
    }

    /**
     * Creates a scheduled ride without a driver (PENDING status)
     */
    public Ride createScheduledRideWithoutDriver() {
        return Ride.builder()
                .id(SAVED_RIDE_ID)
                .status(RideStatus.PENDING)
                .scheduledTime(currentTime.plusHours(2))
                .createdAt(currentTime)
                .driver(null)
                .rideOwner(createValidRideOwner())
                .vehicleType(createValidVehicleType())
                .route(createValidRoute())
                .passengers(List.of())
                .additionalServices(Set.of())
                .build();
    }

    // ==================== Helper Methods ====================

    public List<AppUser> createExistingUsersList() {
        return List.of(createExistingUser1(), createExistingUser2());
    }

    public List<AppUser> createSingleExistingUserList() {
        return List.of(createExistingUser1());
    }

    public List<AppUser> createEmptyUsersList() {
        return new ArrayList<>();
    }
}