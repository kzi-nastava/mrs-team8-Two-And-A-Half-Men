package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.NewRideDTO;
import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.events.DriverAssignedEvent;
import com.project.backend.events.RideCreatedEvent;
import com.project.backend.events.RideStatusChangedEvent;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.*;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.*;
import com.project.backend.service.DateTimeService;
import com.project.backend.service.DriverMatchingService;
import com.project.backend.service.RideBookingService;
import com.project.backend.service.RouteService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideBookingServiceImpl implements RideBookingService {

    private final DriverMatchingService driverMatchingService;
    private final DateTimeService dateTimeService;

    private final RideRepository rideRepository;
    private final PassengerRepository passengerRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final RouteRepository routeRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RouteService routeService;
    private final AppUserRepository appUserRepository;

    @Value("${ride-booking.max-hours}")
    private int MAX_HOURS;


    @Transactional
    @Override
    public NewRideDTO bookRide(Long userId, RideBookingParametersDTO body) {

        var ride = setupRideFromBody(userId, body);

        Double estimatedDistance = null;
        if (ride.getScheduledTime() == null) {
            // Ride is for right now and we need to look for suitable driver
            var driverInfo = driverMatchingService.findDriverFor(ride).orElseThrow(
                    () -> new ResourceNotFoundException("No suitable driver found at this moment with these filters")
            );
            ride.setDriver(driverInfo.getDriver());
            estimatedDistance = driverInfo.getEstimatedDistance();
        }

        rideRepository.save(ride);
        linkRideToPassengers(ride);

        // Send emails to passengers
        applicationEventPublisher.publishEvent(new RideCreatedEvent(ride));

        // If driver is assigned to the ride, send him notification about new ride
        if (ride.getDriver() != null) {
            applicationEventPublisher.publishEvent(new DriverAssignedEvent(ride));
        }

        return new NewRideDTO(ride.getId(), ride.getStatus().toString(), estimatedDistance);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void findDriverForScheduledRide(Long rideId, long minutesBefore) {
        var rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty()) {
            // Ride does not exist, nothing to do
            return;
        }
        var ride = rideOptional.get();
        if (ride.getDriver() != null) {
            // Driver is already assigned, nothing to do
            return;
        }

        if (ride.getStatus() != RideStatus.PENDING) {
            // Ride is not pending, we should not assign driver to it
            return;
        }

        // Look for suitable driver and if found assign him to the ride and change status to ACCEPTED
        var driverInfo = driverMatchingService.findDriverFor(ride).orElse(null);
        if (driverInfo == null) {

            if (minutesBefore == 0) {
                // This is the last try to find a driver, if we don't find him now, we need to cancel the ride
                ride.setStatus(RideStatus.CANCELLED);
                ride.setCancellationReason("No driver found for scheduled ride");
                rideRepository.save(ride);
                applicationEventPublisher.publishEvent(new RideStatusChangedEvent(ride));
            }
            return;
        }
        ride.setDriver(driverInfo.getDriver());
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);

        applicationEventPublisher.publishEvent(new DriverAssignedEvent(ride));
        applicationEventPublisher.publishEvent(new RideStatusChangedEvent(ride));
    }


    /**
     * Creates new {@link Ride} object with basic setup from the body
     * @param userId id of the user that is creating a ride (ride owner)
     * @param body parameters needed to create a ride
     * @return new {@link Ride} object
     */
    private @NonNull Ride setupRideFromBody(Long userId, RideBookingParametersDTO body) {
        var ride = new Ride();

        var rideOwner = fetchRideOwner(userId);
        if (rideOwner.getIsBlocked()) {
            throw new ForbiddenException("You are blocked and cannot book new rides. Reason: "
                    + rideOwner.getBlockReason());
        }
        ride.setRideOwner(rideOwner);

        if (body.getScheduledTime() != null) {
            if ( body.getScheduledTime().isBefore(dateTimeService.getCurrentDateTime()) ||
                    body.getScheduledTime().isAfter(dateTimeService.getCurrentDateTime().plusHours(MAX_HOURS))
            ) {
                throw new BadRequestException("You can only schedule a ride up to " + MAX_HOURS + " hours in the future");
            }
            ride.setScheduledTime(body.getScheduledTime());
            ride.setStatus(RideStatus.PENDING);
        } else {
            ride.setStatus(RideStatus.ACCEPTED);
        }

        if (body.getVehicleTypeId() != null) {
            var vehicleType = fetchVehicleType(body.getVehicleTypeId());
            ride.setVehicleType(vehicleType);
            ride.setPrice(vehicleType.getPrice());
        }

        ride.setAdditionalServices(fetchAdditionalServices(body.getAdditionalServicesIds()));
        ride.setCreatedAt(dateTimeService.getCurrentDateTime());

        if (body.getRouteId() != null) {
            ride.setRoute(fetchExistingRoute(body.getRouteId()));
        } else if (body.getRoute() != null) {
            ride.setRoute(routeService.createNew(body.getRoute()));
        } else {
            throw new BadRequestException("Either routeId or route must be provided");
        }

        ride.setPassengers(createPassengers(rideOwner, body.getPassengers()));

        return ride;
    }

    /**
     * After ride is created and id is provided to the ride by Spring JPA, we can add that ride to every passenger.
     * This method sets {@link Ride} for each {@link Passenger} using {@link Passenger#setRide(Ride)}
     * @param ride ride whose passengers are being linked and also ride to which they are being linked
     */
    private void linkRideToPassengers(Ride ride) {
        for (var passenger : ride.getPassengers()) {
            passenger.setRide(ride);
        }
        passengerRepository.saveAll(ride.getPassengers());
    }


    /**
     * This method fetches user with tha passed id.
     * @param userId id of the user who is booking a ride
     * @return user with the provided id
     * @throws ResourceNotFoundException if the user does not exist
     */
    private AppUser fetchRideOwner(@NotNull Long userId) {
        return appUserRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + userId + " not found"
                        )
                );
    }

    /**
     * Creates new passengers with provided emails and one default passenger representing a
     * ride owner (this passenger is present in all rides), saves them to the database and
     * returns a list of newly created {@link Passenger} objects.
     * If a user exists with provided email his passenger object will link to
     * his {@link AppUser} object. If the user with provided email does not exist
     * in the database, new anonymous passenger is created, meaning his user is null
     * and email is not null.
     * Every passenger has access code setup
     * @param rideOwner user that is creating a ride
     * @param passengerEmails nullable list of emails of passengers
     * @return a list of created passengers
     */
    private @NonNull List<Passenger> createPassengers(@NotNull AppUser rideOwner, List<String> passengerEmails) {
        List<Passenger> passengers = new ArrayList<>();

        // Add ride owner as a passenger
        passengers.add(
                Passenger.builder()
                        .user(rideOwner)
                        .accessToken(UUID.randomUUID().toString())
                        .build()
        );

        if (passengerEmails != null && !passengerEmails.isEmpty()) {
            addPassengersFromEmails(passengerEmails, passengers);
        }
        passengerRepository.saveAll(passengers);
        return passengers;
    }

    /**
     * Creates new passengers with provided emails and adds them to passenger list. If a user exists with provided email
     * his passenger object will link to his {@link AppUser} object. If the user with provided email does not exist
     * in the database, new anonymous passenger is created, meaning his user is null and email is not null.
     * Every passenger has access code setup
     * @param passengerEmails list of emails of passengers
     * @param passengers list in witch to add newly created passengers
     */
    private void addPassengersFromEmails(List<String> passengerEmails, List<Passenger> passengers) {
        var users = appUserRepository.findByEmailIn(passengerEmails);
        for (var passengerEmail : passengerEmails) {
            // Create new Passenger object and setup it's access token
            Passenger passenger = new Passenger();
            passenger.setAccessToken(UUID.randomUUID().toString());

            // Check if user exists and add link it or setup just email
            var user = users.stream()
                    .filter(u -> u.getEmail().equals(passengerEmail))
                    .findFirst();
            if (user.isPresent()) {
                passenger.setUser(user.get());
            } else {
                passenger.setEmail(passengerEmail);
            }

            passengers.add(passenger);
        }
    }

    /**
     * Fetches additional services from the database if any id is provided
     * @param additionalServicesIds ids of the additional services that need to be added to the ride
     * @return set of AdditionalService objects from the database
     * @throws ResourceNotFoundException if any id is not found in the database (does not exist)
     */
    private Set<AdditionalService> fetchAdditionalServices(List<Long> additionalServicesIds) {
        if (additionalServicesIds == null || additionalServicesIds.isEmpty()) {
            return Set.of();
        }
        var additionalServices = additionalServiceRepository.findAllById(additionalServicesIds);
        if (additionalServices.size() != additionalServicesIds.size()) {
            throw new ResourceNotFoundException("One or more additional services not found");
        }
        return Set.copyOf(additionalServices);
    }

    /**
     * Fetches existing route from the database
     * @param existingRouteId id of the route that needs to be fetched
     * @return Route object
     * @throws ResourceNotFoundException if the route with provided id does not exist
     */
    private Route fetchExistingRoute(@NotNull Long existingRouteId) {
        return routeRepository.findById(existingRouteId).orElseThrow(
                () -> new ResourceNotFoundException("Route with id " + existingRouteId + " not found")
        );
    }

    /**
     * Fetches vehicle type with provided id
     * @param vehicleTypeId id of the vehicle type
     * @return vehicle type with specified id
     * @throws ResourceNotFoundException if the vehicle type with provided id doesn't exist
     */
    private VehicleType fetchVehicleType(@NotNull Long vehicleTypeId) {
        return vehicleTypeRepository
                .findById(vehicleTypeId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Vehicle type with id " + vehicleTypeId + " not found"
                        )
                );
    }
}
