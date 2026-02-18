package com.project.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.backend.controllers.fixtures.RideEndRideControllerTestFixture;
import com.project.backend.controllers.ride.RideCommandController;
import com.project.backend.controllers.ride.RideInteractionController;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.NoActiveRideException;
import com.project.backend.models.Driver;
import com.project.backend.service.IRatingService;
import com.project.backend.service.IRideService;
import com.project.backend.service.RideBookingService;
import com.project.backend.service.impl.CancellationService;
import com.project.backend.service.impl.PanicService;
import com.project.backend.service.security.CustomUserDetailsService;
import com.project.backend.util.AuthUtils;
import com.project.backend.util.TokenUtils;
import org.junit.jupiter.api.BeforeEach;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = RideCommandController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfiguration.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("Controller")
@Tag("Student3")
public class RideEndControllerTests {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new JsonMapper();

    private RideEndRideControllerTestFixture fixture;
    @MockitoBean
    private PanicService panicService;
    @MockitoBean
    private  IRideService rideService;
    @MockitoBean
    private  AuthUtils authUtils;
    @MockitoBean
    private  CancellationService cancellationService;
    @MockitoBean
    private TokenUtils tokenUtils;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        fixture = new RideEndRideControllerTestFixture();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void testEndRide_UnAutherized() throws Exception {
        when(authUtils.getCurrentDriver()).thenReturn(null);

        mockMvc.perform(patch("/api/v1/rides/{id}/end", fixture.VALID_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(authUtils, times(1)).getCurrentDriver();
        verify(rideService, never()).endRideById(any(), any());
    }

    @Test
    void testEndRide_RideDontExist() throws Exception {
        Driver driver = fixture.createValidDriver();
        when(authUtils.getCurrentDriver()).thenReturn(driver);
        when(rideService.endRideById(eq(fixture.NON_EXISTENT_RIDE_ID), eq(driver)))
                .thenThrow(new BadRequestException(fixture.ERROR_RIDE_NOT_FOUND));
        mockMvc.perform(patch("/api/v1/rides/{id}/end", fixture.NON_EXISTENT_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(fixture.ERROR_RIDE_NOT_FOUND));

        verify(authUtils, times(1)).getCurrentDriver();
        verify(rideService, times(1)).endRideById(
                eq(fixture.NON_EXISTENT_RIDE_ID),
                eq(driver)
        );
    }
    @Test
    void testEndRide_DriverNotAuth() throws Exception {
        // Arrange
        Driver unauthorizedDriver = fixture.createUnauthorizedDriver();

        when(authUtils.getCurrentDriver()).thenReturn(unauthorizedDriver);
        when(rideService.endRideById(eq(fixture.VALID_RIDE_ID), eq(unauthorizedDriver)))
                .thenThrow(new ForbiddenException(fixture.ERROR_DRIVER_NOT_AUTHORIZED));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/end", fixture.VALID_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(fixture.ERROR_DRIVER_NOT_AUTHORIZED));

        verify(authUtils, times(1)).getCurrentDriver();
        verify(rideService, times(1)).endRideById(
                eq(fixture.VALID_RIDE_ID),
                eq(unauthorizedDriver)
        );
    }
    @Test
    void testEndRide_NoActiveRide() throws Exception {
        Driver driver = fixture.createValidDriver();

        when(authUtils.getCurrentDriver()).thenReturn(driver);
        when(rideService.endRideById(eq(fixture.PENDING_RIDE_ID), eq(driver)))
                .thenThrow(new NoActiveRideException(fixture.ERROR_RIDE_NOT_ACTIVE));

        mockMvc.perform(patch("/api/v1/rides/{id}/end", fixture.PENDING_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(fixture.ERROR_RIDE_NOT_ACTIVE));

        verify(authUtils, times(1)).getCurrentDriver();
        verify(rideService, times(1)).endRideById(
                eq(fixture.PENDING_RIDE_ID),
                eq(driver)
        );
    }
    @Test
    void testEndRide_WhenAuthenticatedDriver_ReturnsOkWithCostAndTime() throws Exception {
        Driver driver = fixture.createValidDriver();
        var expectedResponse = fixture.createSuccessfulEndRideResponse();

        when(authUtils.getCurrentDriver()).thenReturn(driver);
        when(rideService.endRideById(eq(fixture.VALID_RIDE_ID), eq(driver)))
                .thenReturn(expectedResponse);

        mockMvc.perform(patch("/api/v1/rides/{id}/end", fixture.VALID_RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(fixture.TOTAL_COST))
                .andExpect(jsonPath("$.time").value(fixture.DURATION_MINUTES));

        verify(authUtils, times(1)).getCurrentDriver();
        verify(rideService, times(1)).endRideById(fixture.VALID_RIDE_ID, driver);
    }
}
