package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.*;
import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.events.RideFinishedEvent;
import com.project.backend.events.RideStatusChangedEvent;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.NoActiveRideException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.geolocation.metrics.MetricsDistance;
import com.project.backend.models.*;
import com.project.backend.models.actor.PassengerActor;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.*;
import com.project.backend.service.DateTimeService;
import com.project.backend.service.DriverMatchingService;
import com.project.backend.service.IRideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RideService implements IRideService {

    private final CoordinatesFactory coordinatesFactory;
    private final ApplicationEventPublisher applicationEventPublisher;


    private final DriverMatchingService driverMatchingService;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RideTracingService rideTracingService;
    private final LocationTransformer locationTransformer;
    private final CustomerRepository customerRepository;
    private final PassengerRepository passengerRepository;
    private final LocationRepository locationRepository;
    private final RideMapper rideMapper;

    private final ResolvePassengerService resolvePassengerService;
    private final DateTimeService dateTimeService;

    public RideResponseDTO getRideById(Long id, Long currentUserId) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        Set<Route> favourites = null;
        Customer customer = null;
        if (currentUserId != null) {
            customer = customerRepository.findById(currentUserId).orElse(null);
        }
        if (customer != null) {
            favourites = customer.getFavoriteRoutes();
        }

        return rideMapper.convertToRideResponseDTO(ride, favourites);
    }


    @Override
    @Transactional
    public Map<String, Object> startARide(String rideId, Long userId) {
        var driver = driverRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver with id " + userId + " not found"));
        var ride = rideRepository.findById(Long.parseLong(rideId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride with id " + rideId + " not found"));
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new ForbiddenException("You are not assigned to this ride");
        }
        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BadRequestException("Ride is not in ACCEPTED status");
        }
        ride.setStatus(RideStatus.ACTIVE);
        ride.setStartTime(dateTimeService.getCurrentDateTime());
        driver.setDriverStatus(DriverStatus.BUSY);
        rideRepository.save(ride);
        driverRepository.save(driver);

        applicationEventPublisher.publishEvent(new RideStatusChangedEvent(ride));
        return Map.of(
                "message", "Ride started successfully",
                "ok", true,
                "rideStatus", ride.getStatus().toString(),
                "driverStatus", driver.getDriverStatus().toString()
                );
    }

    public NoteResponseDTO saveRideNote(
            Long rideId,
            PassengerActor actor,
            NoteRequestDTO noteRequest
    ) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Active ride with id " + rideId + " not found"));

        Passenger passenger = resolvePassengerService.resolveActor(actor, ride);

        String noteText = noteRequest.getNoteText();
        if (noteText.isBlank() || noteText.length() > 500)
            throw new BadRequestException("Note text length must be between 1 and 500 characters");

        passenger.setInconsistencyNote(noteText);
        passengerRepository.save(passenger);

        return NoteResponseDTO.builder()
                .noteText(noteRequest.getNoteText())
                .passengerMail(passenger.getEmail())
                .rideId(ride.getId())
                .build();
    }

    @Override
    public CostTimeDTO endRideById(Long id, Driver driver) {
        Ride ride = rideRepository.findById(id).orElse(null);

        if(ride == null) {
            throw new BadRequestException("Ride with id " + id + " not found");
        }
        if(!ride.getDriver().getId().equals(driver.getId())) {
            throw new ForbiddenException("Driver not authorized to end this ride");
        }
        if(ride.getStatus() != RideStatus.ACTIVE) {
            throw new NoActiveRideException("Ride is not active");
        }

        String path = rideTracingService.finishRoute(driver.getId());
        ride.setPath(path);
        if(ride.getPrice() == null) {
            ride.setPrice(0.0);
        }
        var distance = locationTransformer.calculateDistanceAir(path, MetricsDistance.KILOMETERS);
        ride.setTotalCost(ride.getPrice() + 120 * distance);
        ride.setDistanceKm(distance);

        ride.setEndTime(dateTimeService.getCurrentDateTime());
        rideRepository.save(ride);
        CostTimeDTO costTimeDTO = new CostTimeDTO();
        costTimeDTO.setCost(ride.getTotalCost());
        LocalDateTime startTime = ride.getStartTime();
        LocalDateTime endTime = ride.getEndTime();
        if(startTime != null && endTime != null) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            costTimeDTO.setTime((double) minutes);
        } else {
            costTimeDTO.setTime(0);
        }
        return costTimeDTO;
    }

    @Override
    public CostTimeDTO estimateRide(RideBookingParametersDTO rideData) {
        FindDriverDTO driver = driverMatchingService.findBestDriver(
                FindDriverFilter.builder()
                        .numberOfPassengers(rideData.getPassengers() != null ? rideData.getPassengers().size() + 1 : 1)
                        .additionalServicesIds(rideData.getAdditionalServicesIds())
                        .vehicleTypeId(rideData.getVehicleTypeId())
                        .latitude(rideData.getRoute().get(0).getLatitude())
                        .longitude(rideData.getRoute().get(0).getLongitude())
                        .build()
        ).orElseThrow(() -> new ResourceNotFoundException("No suitable driver found for the given parameters"));
        double distance = driver.getEstimatedDistance();
        System.out.println("Distance to driver: " + distance);
        var coordinates = coordinatesFactory.getCoordinate(
                rideData.getRoute().get(0).getLatitude(),
                rideData.getRoute().get(0).getLongitude()
        );
        var destCoordinates = coordinatesFactory.getCoordinate(
                rideData.getRoute().get(rideData.getRoute().size() - 1).getLatitude(),
                rideData.getRoute().get(rideData.getRoute().size() - 1).getLongitude()
        );
        double rideDistance = coordinates.distanceAirLine(destCoordinates);
        double estimatedTime = MetricsDistance.KILOMETERS.fromMeters(rideDistance + distance) / 50 * 60;
        return new CostTimeDTO(
                0,
                estimatedTime
        );
    }

    @Override
    public List<RideResponseDTO> getAllBookedRidesByCustomer(Customer customer) {
        List<RideResponseDTO> bookedRides = new ArrayList<>();
        List<Ride> rides = rideRepository.findByRideOwner(customer);
        for(Ride ride : rides) {
            boolean isScheduledNextTenMinutes = ride.getScheduledTime() != null &&
                    ride.getScheduledTime().isAfter(LocalDateTime.now().plusMinutes(10));
            if ((ride.getStatus() == RideStatus.ACCEPTED  || ride.getStatus() == RideStatus.ACTIVE || isScheduledNextTenMinutes) && ride.getStatus() != RideStatus.CANCELLED ) {
                List<Coordinates> coordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
                List<String> hashes = coordinates
                        .stream().map(
                                c -> locationTransformer
                                        .transformFromPoints(List.of(new double[] {c.getLatitude(), c.getLongitude()}))
                        ).toList();
                StringBuilder address = new StringBuilder();
                var locations = locationRepository.findAllByGeoHashIn(hashes);
                for(Location location : locations) {
                    address.append(location.getAddress()).append(" ");
                }
               RideResponseDTO rideBookedDTO = rideMapper.convertToRideResponseDTO(ride);
                bookedRides.add(rideBookedDTO);
            }
        }
        return bookedRides;
    }

    @Transactional
    public RideResponseDTO finishRide(Long id, FinishRideDTO finishRideDTO) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride with id " + id + " not found")
                );

        ride.setStatus(finishRideDTO.isInterrupted() ? RideStatus.INTERRUPTED : RideStatus.FINISHED);
        ride.getDriver().setDriverStatus(DriverStatus.ACTIVE);

        applicationEventPublisher.publishEvent(new RideFinishedEvent(ride));

        rideRepository.save(ride);

        Ride next = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                ride.getDriver(), List.of(RideStatus.ACCEPTED)
        ).orElse(null);

        return next != null ? rideMapper.convertToRideResponseDTO(next) : null;
    }

    @Override
    public List<RideResponseDTO> getMyRides(AppUser currentUser) {
        return rideRepository.findByDriverAndStatusIn(currentUser, List.of(RideStatus.ACCEPTED, RideStatus.ACTIVE))
                .stream().map(rideMapper::convertToRideResponseDTO).toList();
    }

    public List<RideResponseDTO> getActiveRides(
            String driverName
    ) {
        if (driverName == null){
            driverName = "";
        }
        String lowerDriverName = driverName.toLowerCase();
        var drivers = driverRepository.findAll().stream().filter(
                driver -> driver.getFirstName().toLowerCase().contains(lowerDriverName) ||
                        driver.getLastName().toLowerCase().contains(lowerDriverName)
            ).toList();

        List<Ride> rides = rideRepository.findActiveRides(
                List.of(RideStatus.ACTIVE),
                drivers
        );

        return rides
                .stream()
                .map(rideMapper::convertToRideResponseDTO)
                .toList();
    }
}
