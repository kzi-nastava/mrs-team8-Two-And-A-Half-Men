package com.project.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.backend.controllers.fixtures.RideInteractionControllerTestsFixture;
import com.project.backend.controllers.ride.RideInteractionController;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Customer;
import com.project.backend.service.IRatingService;
import com.project.backend.service.IRideService;
import com.project.backend.service.RideBookingService;
import com.project.backend.service.impl.PanicService;
import com.project.backend.service.security.CustomUserDetailsService;
import com.project.backend.util.AuthUtils;
import com.project.backend.util.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller layer tests for RideInteractionController - createRide endpoint.
 * Tests HTTP request/response handling, authentication, and exception handling.

 * Note: Spring Security is disabled for these tests to avoid JWT filter complications.
 * Authentication is mocked via AuthUtils instead.
 */
@WebMvcTest(
        controllers = RideInteractionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfiguration.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("Controller")
public class RideInteractionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new JsonMapper();

    @MockitoBean
    private RideBookingService rideBookingService;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private IRideService rideService;

    @MockitoBean
    private IRatingService ratingService;

    @MockitoBean
    private PanicService panicService;

    @MockitoBean
    private TokenUtils tokenUtils;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private RideInteractionControllerTestsFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new RideInteractionControllerTestsFixture();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    // ==================== Test Case 1: Create ride - successful with authenticated user ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenAuthenticatedUser_ReturnsCreatedWithRideDetails() throws Exception {
        // Arrange
        Customer customer = fixture.createValidCustomer();
        var request = fixture.createMinimalValidRequest();
        var expectedResponse = fixture.createSuccessfulImmediateRideResponse();

        when(authUtils.getCurrentUser()).thenReturn(customer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.CUSTOMER_ID), any()))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(RideInteractionControllerTestsFixture.RIDE_ID))
                .andExpect(jsonPath("$.status").value(RideInteractionControllerTestsFixture.STATUS_ACCEPTED))
                .andExpect(jsonPath("$.estimatedDistance").value(RideInteractionControllerTestsFixture.ESTIMATED_DISTANCE));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.CUSTOMER_ID),
                any()
        );
    }

    // ==================== Test Case 2: Create ride - unauthorized (no authentication) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // Arrange
        var request = fixture.createMinimalValidRequest();
        when(authUtils.getCurrentUser()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(RideInteractionControllerTestsFixture.ERROR_UNAUTHORIZED));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, never()).bookRide(any(), any());
    }

    // ==================== Test Case 3: Create ride - user is blocked (ForbiddenException) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenUserIsBlocked_ReturnsForbidden() throws Exception {
        // Arrange
        Customer blockedCustomer = fixture.createBlockedCustomer();
        var request = fixture.createMinimalValidRequest();

        when(authUtils.getCurrentUser()).thenReturn(blockedCustomer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.BLOCKED_CUSTOMER_ID), any()))
                .thenThrow(new ForbiddenException(RideInteractionControllerTestsFixture.ERROR_USER_BLOCKED));

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(RideInteractionControllerTestsFixture.ERROR_USER_BLOCKED));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.BLOCKED_CUSTOMER_ID),
                any()
        );
    }

    // ==================== Test Case 4: Create ride - invalid scheduled time (BadRequestException) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenInvalidScheduledTime_ReturnsBadRequest() throws Exception {
        // Arrange
        Customer customer = fixture.createValidCustomer();
        var request = fixture.createRequestWithPastScheduledTime();

        when(authUtils.getCurrentUser()).thenReturn(customer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.CUSTOMER_ID), any()))
                .thenThrow(new BadRequestException(RideInteractionControllerTestsFixture.ERROR_INVALID_SCHEDULE_TIME));

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(RideInteractionControllerTestsFixture.ERROR_INVALID_SCHEDULE_TIME));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.CUSTOMER_ID),
                any()
        );
    }

    // ==================== Test Case 5: Create ride - vehicle type not found (ResourceNotFoundException) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenVehicleTypeNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        Customer customer = fixture.createValidCustomer();
        var request = fixture.createRequestWithInvalidVehicleType();

        when(authUtils.getCurrentUser()).thenReturn(customer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.CUSTOMER_ID), any()))
                .thenThrow(new ResourceNotFoundException(RideInteractionControllerTestsFixture.ERROR_VEHICLE_TYPE_NOT_FOUND));

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(RideInteractionControllerTestsFixture.ERROR_VEHICLE_TYPE_NOT_FOUND));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.CUSTOMER_ID),
                any()
        );
    }

    // ==================== Test Case 6: Create ride - no suitable driver found (ResourceNotFoundException) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenNoDriverFound_ReturnsNotFound() throws Exception {
        // Arrange
        Customer customer = fixture.createValidCustomer();
        var request = fixture.createImmediateRideRequest();

        when(authUtils.getCurrentUser()).thenReturn(customer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.CUSTOMER_ID), any()))
                .thenThrow(new ResourceNotFoundException(RideInteractionControllerTestsFixture.ERROR_NO_DRIVER_FOUND));

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(RideInteractionControllerTestsFixture.ERROR_NO_DRIVER_FOUND));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.CUSTOMER_ID),
                any()
        );
    }

    // ==================== Test Case 7: Create ride - with scheduled time (pending status) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenScheduledRide_ReturnsPendingStatus() throws Exception {
        // Arrange
        Customer customer = fixture.createValidCustomer();
        var request = fixture.createScheduledRideRequest();
        var expectedResponse = fixture.createSuccessfulScheduledRideResponse();

        when(authUtils.getCurrentUser()).thenReturn(customer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.CUSTOMER_ID), any()))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(RideInteractionControllerTestsFixture.RIDE_ID))
                .andExpect(jsonPath("$.status").value(RideInteractionControllerTestsFixture.STATUS_PENDING))
                .andExpect(jsonPath("$.estimatedDistance").doesNotExist());

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.CUSTOMER_ID),
                any()
        );
    }

    // ==================== Test Case 8: Create ride - immediate ride (accepted status with driver) ====================


    @Tag("Student1")
    @Test
    void testCreateRide_WhenImmediateRide_ReturnsAcceptedStatusWithDriver() throws Exception {
        // Arrange
        Customer customer = fixture.createValidCustomer();
        var request = fixture.createImmediateRideRequest();
        var expectedResponse = fixture.createSuccessfulImmediateRideResponse();

        when(authUtils.getCurrentUser()).thenReturn(customer);
        when(rideBookingService.bookRide(eq(RideInteractionControllerTestsFixture.CUSTOMER_ID), any()))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(RideInteractionControllerTestsFixture.RIDE_ID))
                .andExpect(jsonPath("$.status").value(RideInteractionControllerTestsFixture.STATUS_ACCEPTED))
                .andExpect(jsonPath("$.estimatedDistance").value(RideInteractionControllerTestsFixture.ESTIMATED_DISTANCE));

        verify(authUtils, times(1)).getCurrentUser();
        verify(rideBookingService, times(1)).bookRide(
                eq(RideInteractionControllerTestsFixture.CUSTOMER_ID),
                any()
        );
    }
}
