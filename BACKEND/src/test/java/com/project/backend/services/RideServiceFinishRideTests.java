package com.project.backend.services;

import com.project.backend.services.fixtures.RideServiceFinishRideTestsFixture;
import com.project.backend.DTO.Ride.FinishRideDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.events.RideFinishedEvent;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.impl.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RideServiceFinishRideTests {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private RideMapper rideMapper;

    @Captor
    private ArgumentCaptor<Ride> rideCaptor;

    @Captor
    private ArgumentCaptor<RideFinishedEvent> eventCaptor;

    private RideService rideService;

    @BeforeEach
    void setUp() {
        rideService = new RideService(
                null,
                applicationEventPublisher,
                null,
                rideRepository,
                null,
                null,
                null,
                null,
                null,
                null,
                rideMapper,
                null,
                null
        );
    }

    // ==================== Test Case 1: Ride does not exist ====================

    @Test
    void testFinishRide_WhenRideDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setIsInterrupted(false);

        when(rideRepository.findById(RideServiceFinishRideTestsFixture.NON_EXISTENT_RIDE_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rideService.finishRide(RideServiceFinishRideTestsFixture.NON_EXISTENT_RIDE_ID, finishRideDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(RideServiceFinishRideTestsFixture.NON_EXISTENT_RIDE_ID.toString());

        verify(rideRepository, times(1)).findById(RideServiceFinishRideTestsFixture.NON_EXISTENT_RIDE_ID);
        verify(rideRepository, never()).save(any(Ride.class));
        verify(applicationEventPublisher, never()).publishEvent(any(RideFinishedEvent.class));
        verify(rideMapper, never()).convertToRideResponseDTO(any(Ride.class));
    }

    // ==================== Test Case 2: Ride finished successfully, no next ride exists ====================

    @Test
    void testFinishRide_WhenRideFinishedSuccessfullyAndNoNextRide_ReturnsNull() {
        // Arrange
        Driver driver = RideServiceFinishRideTestsFixture.createDriver();
        Ride ride = RideServiceFinishRideTestsFixture.createRide(driver, RideStatus.ACTIVE);

        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setIsInterrupted(false);

        when(rideRepository.findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        when(rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver, List.of(RideStatus.ACCEPTED)))
                .thenReturn(Optional.empty());

        // Act
        RideResponseDTO result = rideService.finishRide(RideServiceFinishRideTestsFixture.VALID_RIDE_ID, finishRideDTO);

        // Assert
        assertThat(result).isNull();

        verify(rideRepository, times(1)).findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID);
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideFinishedEvent.class));
        verify(rideMapper, never()).convertToRideResponseDTO(any(Ride.class));

        Ride savedRide = rideCaptor.getValue();
        assertThat(savedRide.getStatus()).isEqualTo(RideStatus.FINISHED);
        assertThat(savedRide.getDriver().getDriverStatus()).isEqualTo(DriverStatus.ACTIVE);
    }

    // ==================== Test Case 3: Ride finished successfully, next ride exists ====================

    @Test
    void testFinishRide_WhenRideFinishedSuccessfullyAndNextRideExists_ReturnsNextRide() {
        // Arrange
        Driver driver = RideServiceFinishRideTestsFixture.createDriver();
        Ride ride = RideServiceFinishRideTestsFixture.createRide(driver, RideStatus.ACTIVE);
        Ride nextRide = RideServiceFinishRideTestsFixture.createNextRide(driver);
        RideResponseDTO expectedDTO = new RideResponseDTO();

        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setIsInterrupted(false);

        when(rideRepository.findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        when(rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver, List.of(RideStatus.ACCEPTED)))
                .thenReturn(Optional.of(nextRide));
        when(rideMapper.convertToRideResponseDTO(nextRide))
                .thenReturn(expectedDTO);

        // Act
        RideResponseDTO result = rideService.finishRide(RideServiceFinishRideTestsFixture.VALID_RIDE_ID, finishRideDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedDTO);

        verify(rideRepository, times(1)).findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID);
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideFinishedEvent.class));
        verify(rideMapper, times(1)).convertToRideResponseDTO(nextRide);

        Ride savedRide = rideCaptor.getValue();
        assertThat(savedRide.getStatus()).isEqualTo(RideStatus.FINISHED);
        assertThat(savedRide.getDriver().getDriverStatus()).isEqualTo(DriverStatus.ACTIVE);
    }

    // ==================== Test Case 4: Ride interrupted successfully ====================

    @Test
    void testFinishRide_WhenRideInterrupted_SetsInterruptedStatus() {
        // Arrange
        Driver driver = RideServiceFinishRideTestsFixture.createDriver();
        Ride ride = RideServiceFinishRideTestsFixture.createRide(driver, RideStatus.ACTIVE);

        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setIsInterrupted(true);

        when(rideRepository.findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        when(rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver, List.of(RideStatus.ACCEPTED)))
                .thenReturn(Optional.empty());

        // Act
        RideResponseDTO result = rideService.finishRide(RideServiceFinishRideTestsFixture.VALID_RIDE_ID, finishRideDTO);

        // Assert
        assertThat(result).isNull();

        verify(rideRepository, times(1)).findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID);
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideFinishedEvent.class));

        Ride savedRide = rideCaptor.getValue();
        assertThat(savedRide.getStatus()).isEqualTo(RideStatus.INTERRUPTED);
        assertThat(savedRide.getDriver().getDriverStatus()).isEqualTo(DriverStatus.ACTIVE);
    }

    // ==================== Test Case 5: Event is published before saving the ride ====================

    @Test
    void testFinishRide_EventPublishedBeforeSavingRide_VerifiesInvocationOrder() {
        // Arrange
        Driver driver = RideServiceFinishRideTestsFixture.createDriver();
        Ride ride = RideServiceFinishRideTestsFixture.createRide(driver, RideStatus.ACTIVE);

        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setIsInterrupted(false);

        when(rideRepository.findById(RideServiceFinishRideTestsFixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        when(rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver, List.of(RideStatus.ACCEPTED)))
                .thenReturn(Optional.empty());

        // Act
        rideService.finishRide(RideServiceFinishRideTestsFixture.VALID_RIDE_ID, finishRideDTO);

        // Assert
        InOrder inOrder = inOrder(applicationEventPublisher, rideRepository);

        inOrder.verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        inOrder.verify(rideRepository).save(rideCaptor.capture());

        RideFinishedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.getRide()).isEqualTo(ride);

        Ride savedRide = rideCaptor.getValue();
        assertThat(savedRide.getStatus()).isEqualTo(RideStatus.FINISHED);
        assertThat(savedRide.getDriver().getDriverStatus()).isEqualTo(DriverStatus.ACTIVE);
    }
}