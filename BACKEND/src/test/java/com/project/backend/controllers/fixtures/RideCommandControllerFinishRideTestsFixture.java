package com.project.backend.controllers.fixtures;

import com.project.backend.DTO.Ride.FinishRideDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.models.enums.RideStatus;

/**
 * Fixture class for RideCommandController.finishRide unit tests.
 * Provides factory methods for creating test DTOs and constants.
 */
public final class RideCommandControllerFinishRideTestsFixture {

    private RideCommandControllerFinishRideTestsFixture() {}

    // ==================== Test Constants ====================

    public static final Long RIDE_ID = 1L;
    public static final Long NEXT_RIDE_ID = 2L;
    public static final Long NON_EXISTENT_ID = 99999L;
    public static final Long ZERO_ID = 0L;
    public static final Long NEGATIVE_ID = -1L;

    public static final String ERROR_RIDE_NOT_FOUND = "Ride with id";

    public static String rideNotFoundError(Long id) {
        return String.format("Ride with id %d not found", id);
    }

    // ==================== DTO Factory Methods ====================

    /**
     * Creates FinishRideDTO with interrupted flag
     */
    public static FinishRideDTO createFinishRideDTO(Boolean interrupted) {
        FinishRideDTO dto = new FinishRideDTO();
        dto.setIsInterrupted(interrupted);
        return dto;
    }

    /**
     * Normal finish
     */
    public static FinishRideDTO createNormalFinishDTO() {
        return createFinishRideDTO(false);
    }

    /**
     * Interrupted finish
     */
    public static FinishRideDTO createInterruptedFinishDTO() {
        return createFinishRideDTO(true);
    }

    /**
     * Creates RideResponseDTO for next ride in queue
     */
    public static RideResponseDTO createNextRideResponse() {
        RideResponseDTO dto = new RideResponseDTO();
        dto.setId(NEXT_RIDE_ID);
        dto.setStatus(RideStatus.ACCEPTED);
        return dto;
    }
}
