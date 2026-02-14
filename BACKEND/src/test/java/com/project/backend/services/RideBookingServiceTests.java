package com.project.backend.services;

import com.project.backend.DTO.Ride.NewRideDTO;
import com.project.backend.events.RideCreatedEvent;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.*;
import com.project.backend.service.DateTimeService;
import com.project.backend.service.DriverMatchingService;
import com.project.backend.service.RideBookingService;
import com.project.backend.service.RouteService;
import com.project.backend.service.impl.RideBookingServiceImpl;
import com.project.backend.services.fixtures.RideBookingServiceTestsFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("Student1")
@Tag("Service")
public class RideBookingServiceTests {

    @Mock
    private DriverMatchingService driverMatchingService;
    @Mock
    private DateTimeService dateTimeService;
    @Mock
    private RideRepository rideRepository;
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private VehicleTypeRepository vehicleTypeRepository;
    @Mock
    private AdditionalServiceRepository additionalServiceRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private RouteService routeService;
    @Mock
    private AppUserRepository appUserRepository;

    @Captor
    private ArgumentCaptor<Ride> rideCaptor;
    @Captor
    private ArgumentCaptor<List<Passenger>> passengersCaptor;

    private RideBookingService rideBookingService;
    private RideBookingServiceTestsFixture fixture;

    private static final int MAX_HOURS = 5;

    @BeforeEach
    void setUp() {
        fixture = new RideBookingServiceTestsFixture();

        when(dateTimeService.getCurrentDateTime()).thenReturn(RideBookingServiceTestsFixture.currentTime);
        rideBookingService = new RideBookingServiceImpl(
                driverMatchingService,
                dateTimeService,
                rideRepository,
                passengerRepository,
                vehicleTypeRepository,
                additionalServiceRepository,
                routeRepository,
                applicationEventPublisher,
                routeService,
                appUserRepository
        );

        ReflectionTestUtils.setField(rideBookingService, "MAX_HOURS", MAX_HOURS);
    }

    // ==================== Test Case 1: Ride owner does not exist ====================

    @Test
    void testBookRide_WhenRideOwnerDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        var request = fixture.createMinimalValidRequest();
        when(appUserRepository.findById(RideBookingServiceTestsFixture.NON_EXISTENT_USER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.NON_EXISTENT_USER_ID, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(RideBookingServiceTestsFixture.NON_EXISTENT_USER_ID.toString());

        verify(appUserRepository, times(1))
                .findById(RideBookingServiceTestsFixture.NON_EXISTENT_USER_ID);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 2: Ride owner's account is blocked ====================

    @Test
    void testBookRide_WhenRideOwnerIsBlocked_ThrowsForbiddenException() {
        // Arrange
        var request = fixture.createMinimalValidRequest();
        var blockedUser = fixture.createBlockedRideOwner();
        when(appUserRepository.findById(RideBookingServiceTestsFixture.BLOCKED_USER_ID))
                .thenReturn(Optional.of(blockedUser));

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.BLOCKED_USER_ID, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining(RideBookingServiceTestsFixture.BLOCK_REASON);

        verify(appUserRepository, times(1))
                .findById(RideBookingServiceTestsFixture.BLOCKED_USER_ID);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 3: Invalid schedule time ====================

    @Test
    void testBookRide_WhenScheduledTimeIsInPast_ThrowsBadRequestException() {
        // Arrange
        var request = fixture.createRequestWithPastScheduledTime();
        var rideOwner = fixture.createValidRideOwner();
        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.valueOf(MAX_HOURS));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void testBookRide_WhenScheduledTimeIsTooFarInFuture_ThrowsBadRequestException() {
        // Arrange
        var request = fixture.createRequestWithTooFarFutureScheduledTime(MAX_HOURS);
        var rideOwner = fixture.createValidRideOwner();
        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(String.valueOf(MAX_HOURS));

        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 4: Vehicle type does not exist ====================

    @Test
    void testBookRide_WhenVehicleTypeDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        var request = fixture.createRequestWithNonExistentVehicleType();
        var rideOwner = fixture.createValidRideOwner();
        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(vehicleTypeRepository.findById(RideBookingServiceTestsFixture.NON_EXISTENT_VEHICLE_TYPE_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(RideBookingServiceTestsFixture.NON_EXISTENT_VEHICLE_TYPE_ID.toString());

        verify(vehicleTypeRepository, times(1))
                .findById(RideBookingServiceTestsFixture.NON_EXISTENT_VEHICLE_TYPE_ID);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 5: Vehicle type is found correctly ====================

    @Test
    void testBookRide_WhenVehicleTypeExists_SetsVehicleTypeAndPriceCorrectly() {
        // Arrange
        var request = fixture.createRequestWithValidVehicleType();
        var rideOwner = fixture.createValidRideOwner();
        var vehicleType = fixture.createValidVehicleType();
        var newRoute = fixture.createNewRoute();
        var savedRide = new Ride();
        savedRide.setId(RideBookingServiceTestsFixture.SAVED_RIDE_ID);

        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(vehicleTypeRepository.findById(RideBookingServiceTestsFixture.VALID_VEHICLE_TYPE_ID))
                .thenReturn(Optional.of(vehicleType));
        when(routeService.createNew(request.getRoute())).thenReturn(newRoute);
        this.mockRideSave();
        when(driverMatchingService.findDriverFor(any(Ride.class)))
                .thenReturn(Optional.of(fixture.createFindDriverDTO()));

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(vehicleTypeRepository, times(1))
                .findById(RideBookingServiceTestsFixture.VALID_VEHICLE_TYPE_ID);
        verify(rideRepository, times(1)).save(rideCaptor.capture());

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getVehicleType()).isEqualTo(vehicleType);
        assertThat(capturedRide.getPrice()).isEqualTo(RideBookingServiceTestsFixture.VEHICLE_TYPE_PRICE);
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);


        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 6: Additional services not specified ====================

    @Test
    void testBookRide_WhenAdditionalServicesIsNull_SavesRideWithEmptyAdditionalServices() {
        // Arrange
        var request = fixture.createRequestWithoutAdditionalServices();
        setupValidRideBookingMocks();

        // Act

        NewRideDTO result = rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getAdditionalServices()).isEmpty();
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);


        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    @Test
    void testBookRide_WhenAdditionalServicesIsEmpty_SavesRideWithEmptyAdditionalServices() {
        // Arrange
        var request = fixture.createRequestWithEmptyAdditionalServices();
        setupValidRideBookingMocks();

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getAdditionalServices()).isEmpty();
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 7: Some additional services not found ====================

    @Test
    void testBookRide_WhenSomeAdditionalServicesNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        var request = fixture.createRequestWithSomeNonExistentAdditionalServices();
        var rideOwner = fixture.createValidRideOwner();
        var vehicleType = fixture.createValidVehicleType();
        var partialServices = fixture.createPartialAdditionalServices();

        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(vehicleTypeRepository.findById(RideBookingServiceTestsFixture.VALID_VEHICLE_TYPE_ID))
                .thenReturn(Optional.of(vehicleType));
        when(additionalServiceRepository.findAllById(request.getAdditionalServicesIds()))
                .thenReturn(partialServices);

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("additional services not found");

        verify(additionalServiceRepository, times(1))
                .findAllById(request.getAdditionalServicesIds());
        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 8: Additional services are found correctly ====================

    @Test
    void testBookRide_WhenAllAdditionalServicesExist_SavesRideWithAdditionalServices() {
        // Arrange
        var request = fixture.createRequestWithValidAdditionalServices();
        var allServices = fixture.createAllAdditionalServices();
        setupValidRideBookingMocks();
        when(additionalServiceRepository.findAllById(request.getAdditionalServicesIds()))
                .thenReturn(allServices);

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(additionalServiceRepository, times(1))
                .findAllById(request.getAdditionalServicesIds());
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getAdditionalServices()).hasSize(2);
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 9: Route id is set and not found ====================

    @Test
    void testBookRide_WhenRouteIdDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        var request = fixture.createRequestWithNonExistentRouteId();
        var rideOwner = fixture.createValidRideOwner();
        var vehicleType = fixture.createValidVehicleType();

        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(vehicleTypeRepository.findById(RideBookingServiceTestsFixture.VALID_VEHICLE_TYPE_ID))
                .thenReturn(Optional.of(vehicleType));
        when(routeRepository.findById(RideBookingServiceTestsFixture.NON_EXISTENT_ROUTE_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(RideBookingServiceTestsFixture.NON_EXISTENT_ROUTE_ID.toString());

        verify(routeRepository, times(1))
                .findById(RideBookingServiceTestsFixture.NON_EXISTENT_ROUTE_ID);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 10: Route id is set and found correctly ====================

    @Test
    void testBookRide_WhenRouteIdExists_SavesRideWithExistingRoute() {
        // Arrange
        var request = fixture.createRequestWithExistingRouteId();
        var existingRoute = fixture.createValidRoute();
        setupValidRideBookingMocks();
        when(routeRepository.findById(RideBookingServiceTestsFixture.VALID_ROUTE_ID))
                .thenReturn(Optional.of(existingRoute));

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(routeRepository, times(1))
                .findById(RideBookingServiceTestsFixture.VALID_ROUTE_ID);
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getRoute()).isEqualTo(existingRoute);
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 11: Route id is not set but route is ====================

    @Test
    void testBookRide_WhenRouteObjectProvided_CreatesNewRoute() {
        // Arrange
        var request = fixture.createRequestWithRouteObject();
        var newRoute = fixture.createNewRoute();
        setupValidRideBookingMocks();
        when(routeService.createNew(request.getRoute())).thenReturn(newRoute);

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(routeService, times(1)).createNew(request.getRoute());
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getRoute()).isEqualTo(newRoute);
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 12: No passenger list provided ====================

    @Test
    void testBookRide_WhenNoPassengersProvided_SavesRideWithOnlyRideOwnerAsPassenger() {
        // Arrange
        var request = fixture.createRequestWithoutPassengers();
        setupValidRideBookingMocks();

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(passengerRepository, times(2)).saveAll(passengersCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        var allPassengersSaves = passengersCaptor.getAllValues();
        var firstSave = allPassengersSaves.get(0);

        assertThat(firstSave).hasSize(1);
        var passenger = firstSave.get(0);
        assertThat(passenger.getEmail()).isNull();
        assertThat(passenger.getUser()).isEqualTo(fixture.createValidRideOwner());
        assertThat(passenger.getAccessToken()).isNotNull();
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 13: Added passengers, only existing users ====================

    @Test
    void testBookRide_WhenAllPassengersAreExistingUsers_SavesRideWithLinkedUsers() {
        // Arrange
        var request = fixture.createRequestWithExistingUserPassengers();
        var existingUsers = fixture.createExistingUsersList();
        setupValidRideBookingMocks();
        when(appUserRepository.findByEmailIn(request.getPassengers()))
                .thenReturn(existingUsers);

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(appUserRepository, times(1)).findByEmailIn(request.getPassengers());
        verify(passengerRepository, times(2)).saveAll(passengersCaptor.capture());

        var allPassengersSaves = passengersCaptor.getAllValues();
        var firstSave = allPassengersSaves.get(0);

        // Should have ride owner + 2 passengers
        assertThat(firstSave).hasSize(3);

        // Check ride owner passenger
        var ownerPassenger = firstSave.get(0);
        assertThat(ownerPassenger.getEmail()).isNull();
        assertThat(ownerPassenger.getUser()).isNotNull();
        assertThat(ownerPassenger.getUser().getId()).isEqualTo(RideBookingServiceTestsFixture.VALID_USER_ID);

        // Check other passengers have user linked and email null
        var otherPassengers = firstSave.subList(1, 3);
        for (var passenger : otherPassengers) {
            assertThat(passenger.getEmail()).isNull();
            assertThat(passenger.getUser()).isNotNull();
            assertThat(passenger.getAccessToken()).isNotNull();
        }

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 14: Added passengers, only non-existing users ====================

    @Test
    void testBookRide_WhenAllPassengersAreNonExistingUsers_SavesRideWithEmailsOnly() {
        // Arrange
        var request = fixture.createRequestWithNonExistingUserPassengers();
        setupValidRideBookingMocks();
        when(appUserRepository.findByEmailIn(request.getPassengers()))
                .thenReturn(fixture.createEmptyUsersList());

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(appUserRepository, times(1)).findByEmailIn(request.getPassengers());
        verify(passengerRepository, times(2)).saveAll(passengersCaptor.capture());

        var allPassengersSaves = passengersCaptor.getAllValues();
        var firstSave = allPassengersSaves.get(0);

        // Should have ride owner + 2 passengers
        assertThat(firstSave).hasSize(3);

        // Check ride owner passenger
        var ownerPassenger = firstSave.get(0);
        assertThat(ownerPassenger.getEmail()).isNull();
        assertThat(ownerPassenger.getUser()).isNotNull();

        // Check other passengers have email and user null
        var otherPassengers = firstSave.subList(1, 3);
        assertThat(otherPassengers).allMatch(p -> p.getUser() == null);
        assertThat(otherPassengers).allMatch(p -> p.getEmail() != null);
        assertThat(otherPassengers).extracting(Passenger::getEmail)
                .containsExactlyInAnyOrder(
                        RideBookingServiceTestsFixture.NON_EXISTING_USER_EMAIL_1,
                        RideBookingServiceTestsFixture.NON_EXISTING_USER_EMAIL_2
                );

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 15: Added passengers, mix of existing and non-existing users ====================

    @Test
    void testBookRide_WhenPassengersAreMixed_SavesRideWithCorrectPassengerData() {
        // Arrange
        var request = fixture.createRequestWithMixedPassengers();
        var existingUsers = fixture.createSingleExistingUserList();
        setupValidRideBookingMocks();
        when(appUserRepository.findByEmailIn(request.getPassengers()))
                .thenReturn(existingUsers);

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(appUserRepository, times(1)).findByEmailIn(request.getPassengers());
        verify(passengerRepository, times(2)).saveAll(passengersCaptor.capture());

        var allPassengersSaves = passengersCaptor.getAllValues();
        var firstSave = allPassengersSaves.get(0);

        // Should have ride owner + 2 passengers
        assertThat(firstSave).hasSize(3);

        // Check ride owner
        var ownerPassenger = firstSave.get(0);
        assertThat(ownerPassenger.getUser()).isNotNull();
        assertThat(ownerPassenger.getEmail()).isNull();

        // Check mix: one with user, one with email
        var otherPassengers = firstSave.subList(1, 3);
        long passengersWithUser = otherPassengers.stream().filter(p -> p.getUser() != null).count();
        long passengersWithEmail = otherPassengers.stream().filter(p -> p.getEmail() != null).count();

        assertThat(passengersWithUser).isEqualTo(1);
        assertThat(passengersWithEmail).isEqualTo(1);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Test Case 16: Valid schedule time is set ====================

    @Test
    void testBookRide_WhenValidScheduledTimeProvided_SavesRideWithPendingStatus() {
        // Arrange
        var request = fixture.createRequestWithValidScheduledTime();
        setupValidRideBookingMocksWithoutDriver();

        // Act
        NewRideDTO result = rideBookingService.bookRide(RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getScheduledTime()).isEqualTo(request.getScheduledTime());
        assertThat(capturedRide.getStatus()).isEqualTo(RideStatus.PENDING);
        assertThat(capturedRide.getDriver()).isNull();
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.PENDING.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(null);
    }

    // ==================== Test Case 17: No suitable driver found ====================

    @Test
    void testBookRide_WhenNoSuitableDriverFound_ThrowsResourceNotFoundException() {
        // Arrange
        var request = fixture.createRequestWithoutScheduledTime();
        var rideOwner = fixture.createValidRideOwner();
        var vehicleType = fixture.createValidVehicleType();
        var newRoute = fixture.createNewRoute();

        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(vehicleTypeRepository.findById(RideBookingServiceTestsFixture.VALID_VEHICLE_TYPE_ID))
                .thenReturn(Optional.of(vehicleType));
        when(routeService.createNew(request.getRoute())).thenReturn(newRoute);
        when(driverMatchingService.findDriverFor(any(Ride.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No suitable driver found");

        verify(driverMatchingService, times(1)).findDriverFor(any(Ride.class));
        verify(rideRepository, never()).save(any(Ride.class));
    }

    // ==================== Test Case 18: Driver is found and assigned to the ride ====================

    @Test
    void testBookRide_WhenDriverFound_SavesRideWithDriverAndAcceptedStatus() {
        // Arrange
        var request = fixture.createRequestWithoutScheduledTime();
        var driverDTO = fixture.createFindDriverDTO();
        setupValidRideBookingMocks();
        when(driverMatchingService.findDriverFor(any(Ride.class)))
                .thenReturn(Optional.of(driverDTO));

        // Act
        NewRideDTO result = rideBookingService.bookRide(
                RideBookingServiceTestsFixture.VALID_USER_ID, request);

        // Assert
        verify(driverMatchingService, times(1)).findDriverFor(any(Ride.class));
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        verify(applicationEventPublisher, times(1)).publishEvent(any(RideCreatedEvent.class));

        var capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getDriver()).isEqualTo(driverDTO.getDriver());
        assertThat(capturedRide.getStatus()).isEqualTo(RideStatus.ACCEPTED);
        assertThat(capturedRide.getScheduledTime()).isNull();
        assertThat(capturedRide.getCreatedAt()).isEqualTo(RideBookingServiceTestsFixture.currentTime);

        // Verify returned DTO
        assertThat(result.getId()).isEqualTo(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        assertThat(result.getStatus()).isEqualTo(RideStatus.ACCEPTED.toString());
        assertThat(result.getEstimatedDistance()).isEqualTo(RideBookingServiceTestsFixture.ESTIMATED_DISTANCE);
    }

    // ==================== Helper Methods ====================

    /**
     * Sets up all common mocks for a valid ride booking scenario (with driver)
     */
    private void setupValidRideBookingMocks() {
        var rideOwner = fixture.createValidRideOwner();
        var newRoute = fixture.createNewRoute();
        var savedRide = new Ride();
        savedRide.setId(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
        var driverDTO = fixture.createFindDriverDTO();

        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(routeService.createNew(any())).thenReturn(newRoute);
        this.mockRideSave();
        when(driverMatchingService.findDriverFor(any(Ride.class)))
                .thenReturn(Optional.of(driverDTO));
    }

    /**
     * Sets up all common mocks for a valid ride booking scenario (without driver - for scheduled rides)
     */
    private void setupValidRideBookingMocksWithoutDriver() {
        var rideOwner = fixture.createValidRideOwner();
        var vehicleType = fixture.createValidVehicleType();
        var newRoute = fixture.createNewRoute();
        var savedRide = new Ride();
        savedRide.setId(RideBookingServiceTestsFixture.SAVED_RIDE_ID);

        when(appUserRepository.findById(RideBookingServiceTestsFixture.VALID_USER_ID))
                .thenReturn(Optional.of(rideOwner));
        when(vehicleTypeRepository.findById(RideBookingServiceTestsFixture.VALID_VEHICLE_TYPE_ID))
                .thenReturn(Optional.of(vehicleType));
        when(routeService.createNew(any())).thenReturn(newRoute);
        this.mockRideSave();
    }

    /**
     * Mocks the rideRepository.save() method to add an ID to the saved ride and return it, simulating the behavior of a real repository save.
     */
    private void mockRideSave() {
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            ride.setId(RideBookingServiceTestsFixture.SAVED_RIDE_ID);
            return ride;
        });
    }
}