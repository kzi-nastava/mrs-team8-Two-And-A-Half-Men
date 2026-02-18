package com.project.backend.services;

import com.project.backend.DTO.Ride.CostTimeDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.NoActiveRideException;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.geolocation.metrics.MetricsDistance;
import com.project.backend.models.Ride;
import com.project.backend.repositories.*;
import com.project.backend.service.DateTimeService;
import com.project.backend.service.DriverMatchingService;
import com.project.backend.service.impl.ResolvePassengerService;
import com.project.backend.service.impl.RideService;
import com.project.backend.service.impl.RideTracingService;
import com.project.backend.services.fixtures.RideEndingRideServiceTestsFixture;
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

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("Service")
@Tag("Student3")
public class RideServiceEndRideTest {

    RideEndingRideServiceTestsFixture fixture;
    private RideService rideService;
    @Mock
    private DriverMatchingService driverMatchingService;
    @Mock
    private RideRepository rideRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private RideTracingService rideTracingService;
    @Mock
    private LocationTransformer locationTransformer;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private RideMapper rideMapper;
    @Mock
    private ResolvePassengerService resolvePassengerService;
    @Mock
    private DateTimeService dateTimeService;
    @Mock
    private  CoordinatesFactory coordinatesFactory;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Captor
    private ArgumentCaptor<Ride> rideCaptor;

    @BeforeEach
    void setUp() {

        fixture = new RideEndingRideServiceTestsFixture();
        rideService = new RideService(coordinatesFactory, applicationEventPublisher, driverMatchingService, rideRepository, driverRepository,
                rideTracingService, locationTransformer, customerRepository, passengerRepository, locationRepository,
                rideMapper, resolvePassengerService, dateTimeService);
        when(dateTimeService.getCurrentDateTime()).thenReturn(fixture.endTime);
    }
    @Test
    void testEndRideById_RideNotExist() {
        var driver = fixture.createValidDriver();
        when(rideRepository.findById(fixture.NON_EXISTENT_RIDE_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> rideService.endRideById(
                fixture.NON_EXISTENT_RIDE_ID, driver))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ride with id " + this.fixture.NON_EXISTENT_RIDE_ID + " not found");

        verify(rideRepository, times(1)).findById(fixture.NON_EXISTENT_RIDE_ID);
        verify(rideTracingService, never()).finishRoute(driver.getId());
        verify(locationTransformer , never()).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , never()).getCurrentDateTime();
        verify(rideRepository, never()).save(any(Ride.class));
    }
    @Test
    void testEndRideById_DriverNotAuth() {
        var unauthorizedDriver = fixture.createUnauthorizedDriver();
        var ride = fixture.createActiveRide();
        when(rideRepository.findById(fixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));

        assertThatThrownBy(() -> rideService.endRideById(
                fixture.VALID_RIDE_ID, unauthorizedDriver))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Driver not authorized to end this ride");

        verify(rideRepository, times(1))
                .findById(fixture.VALID_RIDE_ID);
        verify(rideTracingService, never()).finishRoute(any());
        verify(locationTransformer , never()).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , never()).getCurrentDateTime();
        verify(rideRepository, never()).save(any(Ride.class));
    }
    @Test
    void testEndRideById_RideIsPending() {
        var driver = fixture.createValidDriver();
        var ride = fixture.createPendingRide();
        when(rideRepository.findById(fixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));

        assertThatThrownBy(() -> rideService.endRideById(
                fixture.VALID_RIDE_ID, driver))
                .isInstanceOf(NoActiveRideException.class)
                .hasMessageContaining("Ride is not active");

        verify(rideRepository, times(1))
                .findById(fixture.VALID_RIDE_ID);
        verify(rideTracingService, never()).finishRoute(driver.getId());
        verify(locationTransformer , never()).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , never()).getCurrentDateTime();
        verify(rideRepository, never()).save(any(Ride.class));
    }
    @Test
    void testEndRideById_RideIsActive() {
        // Arrange
        var driver = fixture.createValidDriver();
        var ride = fixture.createAcceptedRide();
        when(rideRepository.findById(fixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));

        assertThatThrownBy(() -> rideService.endRideById(
                fixture.VALID_RIDE_ID, driver))
                .isInstanceOf(NoActiveRideException.class)
                .hasMessageContaining("Ride is not active");

        verify(rideRepository, times(1))
                .findById(fixture.VALID_RIDE_ID);
        verify(rideTracingService, never()).finishRoute(driver.getId());
        verify(locationTransformer , never()).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , never()).getCurrentDateTime();
        verify(rideRepository, never()).save(any(Ride.class));
    }
    private void setupSuccessfulEndRideMocks() {
        when(rideTracingService.finishRoute(fixture.VALID_DRIVER_ID))
                .thenReturn(fixture.FINISHED_PATH);
        when(locationTransformer.calculateDistanceAir(
                fixture.FINISHED_PATH,
                MetricsDistance.KILOMETERS))
                .thenReturn(fixture.RIDE_DISTANCE);
        when(dateTimeService.getCurrentDateTime())
                .thenReturn(fixture.endTime);
    }
    @Test
    void testEndRideById_priceIsNull()
    {
        var driver = fixture.createValidDriver();
        var ride = fixture.createActiveRideWithNullPrice();
        setupSuccessfulEndRideMocks();
        when(rideRepository.findById(fixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        CostTimeDTO result = rideService.endRideById(
                fixture.VALID_RIDE_ID, driver);

        verify(rideTracingService, times(1)).finishRoute(driver.getId());
        verify(locationTransformer , times(1)).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , times(1)).getCurrentDateTime();
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        Ride capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getPrice()).isEqualTo(0.0);
        assertThat(capturedRide.getTotalCost()).isEqualTo(120 * fixture.RIDE_DISTANCE);
        assertThat(capturedRide.getDistanceKm()).isEqualTo(fixture.RIDE_DISTANCE);
        assertThat(capturedRide.getPath()).isEqualTo(fixture.FINISHED_PATH);
        assertThat(capturedRide.getEndTime()).isEqualTo(fixture.endTime);
        verify(rideRepository, times(1))
                .findById(fixture.VALID_RIDE_ID);

        assertThat(result.getCost()).isEqualTo(120 * fixture.RIDE_DISTANCE);
        assertThat(result.getTime()).isEqualTo(fixture.EXPECTED_DURATION_MINUTES);
    }
    @Test
    void testEndRideById_timeIsNull()
    {
        var driver = fixture.createValidDriver();
        var ride = fixture.createActiveRideWithNullTimes();
        setupSuccessfulEndRideMocks();
        when(rideRepository.findById(fixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        CostTimeDTO result = rideService.endRideById(
                fixture.VALID_RIDE_ID, driver);
        verify(rideTracingService, times(1)).finishRoute(driver.getId());
        verify(locationTransformer , times(1)).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , times(1)).getCurrentDateTime();
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        Ride capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getPrice()).isEqualTo(fixture.RIDE_PRICE);
        assertThat(capturedRide.getTotalCost()).isEqualTo(120 * fixture.RIDE_DISTANCE + fixture.RIDE_PRICE);
        assertThat(capturedRide.getDistanceKm()).isEqualTo(fixture.RIDE_DISTANCE);
        assertThat(capturedRide.getPath()).isEqualTo(fixture.FINISHED_PATH);
        assertThat(capturedRide.getEndTime()).isEqualTo(fixture.endTime);
        verify(rideRepository, times(1))
                .findById(fixture.VALID_RIDE_ID);

        assertThat(result.getCost()).isEqualTo(120 * fixture.RIDE_DISTANCE + fixture.RIDE_PRICE);
        assertThat(result.getTime()).isEqualTo(0);
    }
    @Test
    void testEndRideById_happyPath() {
        var driver = fixture.createValidDriver();
        var ride = fixture.createActiveRide();
        setupSuccessfulEndRideMocks();
        when(rideRepository.findById(fixture.VALID_RIDE_ID))
                .thenReturn(Optional.of(ride));
        CostTimeDTO result = rideService.endRideById(
                fixture.VALID_RIDE_ID, driver);
        verify(rideTracingService, times(1)).finishRoute(driver.getId());
        verify(locationTransformer , times(1)).calculateDistanceAir(fixture.FINISHED_PATH , MetricsDistance.KILOMETERS);
        verify(dateTimeService , times(1)).getCurrentDateTime();
        verify(rideRepository, times(1)).save(rideCaptor.capture());
        Ride capturedRide = rideCaptor.getValue();
        assertThat(capturedRide.getPrice()).isEqualTo(fixture.RIDE_PRICE);
        assertThat(capturedRide.getTotalCost()).isEqualTo(fixture.CALCULATED_TOTAL_COST);
        assertThat(capturedRide.getDistanceKm()).isEqualTo(fixture.RIDE_DISTANCE);
        assertThat(capturedRide.getPath()).isEqualTo(fixture.FINISHED_PATH);
        assertThat(capturedRide.getEndTime()).isEqualTo(fixture.endTime);
        verify(rideRepository, times(1))
                .findById(fixture.VALID_RIDE_ID);

        assertThat(result.getCost()).isEqualTo(fixture.CALCULATED_TOTAL_COST);
        assertThat(result.getTime()).isEqualTo(fixture.EXPECTED_DURATION_MINUTES);
    }


}
