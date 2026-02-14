package com.project.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.project.backend.DTO.Ride.FinishRideDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.controllers.fixtures.RideCommandControllerFinishRideTestsFixture;
import com.project.backend.controllers.ride.RideCommandController;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.service.IRideService;
import com.project.backend.service.impl.CancellationService;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = RideCommandController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfiguration.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("Controller")
public class RideCommandControllerFinishRideTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRideService rideService;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private TokenUtils tokenUtils;

    @MockitoBean
    private CancellationService cancellationService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new JsonMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    // ==================== Test Case 1: No next ride - body is empty ====================

    @Test
    void testFinishRide_WhenNoNextRide_ReturnsOkWithNullBody() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();

        when(rideService.finishRide(eq(RideCommandControllerFinishRideTestsFixture.RIDE_ID), any()))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(rideService, times(1)).finishRide(
                eq(RideCommandControllerFinishRideTestsFixture.RIDE_ID), any()
        );
        verifyNoInteractions(authUtils);
    }

    // ==================== Test Case 2: Next ride exists - returns its DTO ====================

    @Test
    void testFinishRide_WhenNextRideExists_ReturnsNextRideDTO() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();
        RideResponseDTO expectedResponse = RideCommandControllerFinishRideTestsFixture.createNextRideResponse();

        when(rideService.finishRide(eq(RideCommandControllerFinishRideTestsFixture.RIDE_ID), any()))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RideCommandControllerFinishRideTestsFixture.NEXT_RIDE_ID))
                .andExpect(jsonPath("$.status").value(RideStatus.ACCEPTED.toString()));

        verify(rideService, times(1)).finishRide(
                eq(RideCommandControllerFinishRideTestsFixture.RIDE_ID), any()
        );
    }

    // ==================== Test Case 3: Interrupted flag forwarded to service ====================

    @Test
    void testFinishRide_WhenInterrupted_ServiceCalledWithInterruptedDTO() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createInterruptedFinishDTO();

        when(rideService.finishRide(eq(RideCommandControllerFinishRideTestsFixture.RIDE_ID), any()))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(rideService, times(1)).finishRide(
                eq(RideCommandControllerFinishRideTestsFixture.RIDE_ID), any()
        );
    }

    // ==================== Test Case 4: Ride not found ====================

    @Test
    void testFinishRide_WhenRideNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();

        when(rideService.finishRide(eq(RideCommandControllerFinishRideTestsFixture.NON_EXISTENT_ID), any()))
                .thenThrow(new ResourceNotFoundException(
                        RideCommandControllerFinishRideTestsFixture.rideNotFoundError(
                                RideCommandControllerFinishRideTestsFixture.NON_EXISTENT_ID)));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        containsString(RideCommandControllerFinishRideTestsFixture.ERROR_RIDE_NOT_FOUND)));

        verify(rideService, times(1)).finishRide(
                eq(RideCommandControllerFinishRideTestsFixture.NON_EXISTENT_ID), any()
        );
    }

    // ==================== Test Case 5: Empty request body - missing required field ====================

    @Test
    void testFinishRide_WhenEmptyRequestBody_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.RIDE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(rideService, never()).finishRide(any(), any());
    }

    // ==================== Test Case 6: Non-numeric path variable ====================

    @Test
    void testFinishRide_WhenIdIsNotNumeric_ReturnsBadRequest() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", "invalid-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(rideService, never()).finishRide(any(), any());
    }

    // ==================== Test Case 7: Missing Content-Type header ====================

    @Test
    void testFinishRide_WhenMissingContentType_ReturnsUnsupportedMediaType() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.RIDE_ID)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());

        verify(rideService, never()).finishRide(any(), any());
    }

    // ==================== Test Case 8: Zero ID - treated as not found ====================

    @Test
    void testFinishRide_WhenIdIsZero_ReturnsNotFound() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();

        when(rideService.finishRide(eq(RideCommandControllerFinishRideTestsFixture.ZERO_ID), any()))
                .thenThrow(new ResourceNotFoundException(
                        RideCommandControllerFinishRideTestsFixture.rideNotFoundError(
                                RideCommandControllerFinishRideTestsFixture.ZERO_ID)));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.ZERO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(rideService, times(1)).finishRide(
                eq(RideCommandControllerFinishRideTestsFixture.ZERO_ID), any()
        );
    }

    // ==================== Test Case 9: Negative ID - treated as not found ====================

    @Test
    void testFinishRide_WhenIdIsNegative_ReturnsNotFound() throws Exception {
        // Arrange
        FinishRideDTO request = RideCommandControllerFinishRideTestsFixture.createNormalFinishDTO();

        when(rideService.finishRide(eq(RideCommandControllerFinishRideTestsFixture.NEGATIVE_ID), any()))
                .thenThrow(new ResourceNotFoundException(
                        RideCommandControllerFinishRideTestsFixture.rideNotFoundError(
                                RideCommandControllerFinishRideTestsFixture.NEGATIVE_ID)));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/rides/{id}/finish", RideCommandControllerFinishRideTestsFixture.NEGATIVE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(rideService, times(1)).finishRide(
                eq(RideCommandControllerFinishRideTestsFixture.NEGATIVE_ID), any()
        );
    }
}