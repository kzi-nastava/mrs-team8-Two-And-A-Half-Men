package com.project.backend.controllers.fixtures;

import com.project.backend.DTO.Ride.NewRideDTO;
import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Route.RouteItemDTO;
import com.project.backend.models.Customer;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fixture class for RideInteractionController tests.
 * Provides factory methods for creating test data, DTOs, and mock responses.
 */
@Getter
public class RideInteractionControllerTestsFixture {

    // Test user IDs
    public static final Long CUSTOMER_ID = 1L;
    public static final Long BLOCKED_CUSTOMER_ID = 2L;

    // Test ride IDs
    public static final Long RIDE_ID = 100L;

    // Test vehicle type IDs
    public static final Long VALID_VEHICLE_TYPE_ID = 1L;
    public static final Long NON_EXISTENT_VEHICLE_TYPE_ID = 999L;

    // Test additional service IDs
    public static final Long ADDITIONAL_SERVICE_ID_1 = 1L;
    public static final Long ADDITIONAL_SERVICE_ID_2 = 2L;

    // Test emails
    public static final String CUSTOMER_EMAIL = "customer@example.com";
    public static final String BLOCKED_CUSTOMER_EMAIL = "blocked@example.com";
    public static final String PASSENGER_EMAIL_1 = "passenger1@example.com";
    public static final String PASSENGER_EMAIL_2 = "passenger2@example.com";

    // Test prices and distances
    public static final Double ESTIMATED_DISTANCE = 5.5;
    public static final Double VEHICLE_PRICE = 15.0;

    // Test statuses
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";

    // Error messages
    public static final String ERROR_UNAUTHORIZED = "Unauthorized";
    public static final String ERROR_USER_BLOCKED = "You are blocked and cannot book new rides. Reason: Violated terms of service";
    public static final String ERROR_INVALID_SCHEDULE_TIME = "You can only schedule a ride up to 5 hours in the future";
    public static final String ERROR_VEHICLE_TYPE_NOT_FOUND = "Vehicle type with id " + NON_EXISTENT_VEHICLE_TYPE_ID + " not found";
    public static final String ERROR_NO_DRIVER_FOUND = "No suitable driver found at this moment with these filters";

    private final LocalDateTime currentTime;

    public RideInteractionControllerTestsFixture() {
        this.currentTime = LocalDateTime.of(2026, 5, 31, 12, 0);
    }

    // ==================== Customer Factory Methods ====================

    /**
     * Creates a valid authenticated customer
     */
    public Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setPassword("password123");
        customer.setPhoneNumber("+1234567890");
        customer.setIsActive(true);
        customer.setIsBlocked(false);
        return customer;
    }

    /**
     * Creates a blocked customer
     */
    public Customer createBlockedCustomer() {
        Customer customer = new Customer();
        customer.setId(BLOCKED_CUSTOMER_ID);
        customer.setFirstName("Blocked");
        customer.setLastName("User");
        customer.setEmail(BLOCKED_CUSTOMER_EMAIL);
        customer.setPassword("password123");
        customer.setPhoneNumber("+1234567890");
        customer.setIsActive(true);
        customer.setIsBlocked(true);
        customer.setBlockReason("Violated terms of service");
        return customer;
    }

    // ==================== Route Factory Methods ====================

    /**
     * Creates a list of route items for a valid route
     */
    public List<RouteItemDTO> createRouteItems() {
        return List.of(
                new RouteItemDTO("123 Main St", 40.7128, -74.0060),
                new RouteItemDTO("456 Elm St", 40.7580, -73.9855)
        );
    }

    // ==================== RideBookingParametersDTO Factory Methods ====================

    /**
     * Creates a minimal valid ride booking request (immediate ride, no passengers)
     */
    public RideBookingParametersDTO createMinimalValidRequest() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .build();
    }

    /**
     * Creates a complete ride booking request with all optional fields
     */
    public RideBookingParametersDTO createCompleteRequest() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .passengers(List.of(PASSENGER_EMAIL_1, PASSENGER_EMAIL_2))
                .additionalServicesIds(List.of(ADDITIONAL_SERVICE_ID_1, ADDITIONAL_SERVICE_ID_2))
                .build();
    }

    /**
     * Creates a request for a scheduled ride
     */
    public RideBookingParametersDTO createScheduledRideRequest() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .scheduledTime(currentTime.plusHours(2))
                .build();
    }

    /**
     * Creates a request with invalid scheduled time (in the past)
     */
    public RideBookingParametersDTO createRequestWithPastScheduledTime() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .scheduledTime(currentTime.minusHours(1))
                .build();
    }

    /**
     * Creates a request with invalid scheduled time (too far in future)
     */
    public RideBookingParametersDTO createRequestWithTooFarFutureScheduledTime() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .scheduledTime(currentTime.plusHours(10))
                .build();
    }

    /**
     * Creates a request with non-existent vehicle type
     */
    public RideBookingParametersDTO createRequestWithInvalidVehicleType() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(NON_EXISTENT_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .build();
    }

    /**
     * Creates a request for immediate ride (no scheduled time)
     */
    public RideBookingParametersDTO createImmediateRideRequest() {
        return RideBookingParametersDTO.builder()
                .vehicleTypeId(VALID_VEHICLE_TYPE_ID)
                .route(createRouteItems())
                .scheduledTime(null) // Immediate ride
                .build();
    }

    // ==================== NewRideDTO Factory Methods ====================

    /**
     * Creates a successful response for an immediate ride (with driver)
     */
    public NewRideDTO createSuccessfulImmediateRideResponse() {
        return NewRideDTO.builder()
                .id(RIDE_ID)
                .status(STATUS_ACCEPTED)
                .estimatedDistance(ESTIMATED_DISTANCE)
                .build();
    }

    /**
     * Creates a successful response for a scheduled ride (no driver yet)
     */
    public NewRideDTO createSuccessfulScheduledRideResponse() {
        return NewRideDTO.builder()
                .id(RIDE_ID)
                .status(STATUS_PENDING)
                .estimatedDistance(null) // No driver assigned yet
                .build();
    }

    // ==================== JSON String Factory Methods ====================

    /**
     * Creates a JSON string for minimal valid request
     */
    public String createMinimalValidRequestJson() {
        return """
                {
                    "vehicleTypeId": 1,
                    "route": [
                        {
                            "address": "123 Main St",
                            "latitude": 40.7128,
                            "longitude": -74.0060
                        },
                        {
                            "address": "456 Elm St",
                            "latitude": 40.7580,
                            "longitude": -73.9855
                        }
                    ]
                }
                """;
    }

    /**
     * Creates a JSON string for scheduled ride request
     */
    public String createScheduledRideRequestJson() {
        return String.format("""
                {
                    "vehicleTypeId": 1,
                    "route": [
                        {
                            "address": "123 Main St",
                            "latitude": 40.7128,
                            "longitude": -74.0060
                        },
                        {
                            "address": "456 Elm St",
                            "latitude": 40.7580,
                            "longitude": -73.9855
                        }
                    ],
                    "scheduledTime": "%s"
                }
                """, currentTime.plusHours(2).toString());
    }

    /**
     * Creates a JSON string for complete request with all fields
     */
    public String createCompleteRequestJson() {
        return """
                {
                    "vehicleTypeId": 1,
                    "route": [
                        {
                            "address": "123 Main St",
                            "latitude": 40.7128,
                            "longitude": -74.0060
                        },
                        {
                            "address": "456 Elm St",
                            "latitude": 40.7580,
                            "longitude": -73.9855
                        }
                    ],
                    "passengers": ["passenger1@example.com", "passenger2@example.com"],
                    "additionalServicesIds": [1, 2]
                }
                """;
    }
}