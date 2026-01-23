package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.NewRideDTO;
import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.DTO.redis.RedisLocationsDTO;
import com.project.backend.events.RideCreatedEvent;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.exceptions.UnauthenticatedException;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.exceptions.UnauthenticatedException;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.models.*;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.Route;
import com.project.backend.models.Route;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.*;
import com.project.backend.repositories.redis.DriverLocationsRepository;
import com.project.backend.repositories.RouteRepository;
import com.project.backend.repositories.RouteRepository;
import com.project.backend.service.IRideService;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RideService implements IRideService {

    private final DriverLocationsRepository driverLocationsRepository;
    private final CoordinatesFactory coordinatesFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${ride-booking.max-hours}")
    private int MAX_HOURS;

    private final List<Integer> KM_RANGE = List.of(30, 50, 70, 100, -1);

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RideTracingService rideTracingService;
    private final LocationTransformer locationTransformer;
    private final RouteRepository routeRepository;
    private final RideTracingService rideTracingService;
    private final LocationTransformer locationTransformer;
    private final RouteRepository routeRepository;
    private final RouteRepository routeRepository;
    private final LocationTransformer locationTransformer;
    private final LocationRepository locationRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final PassengerRepository passengerRepository;

    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        return RideMapper.convertToHistoryResponseDTO(ride);
    }

    @Transactional
    @Override
    public NewRideDTO createRide(Long userId, RideBookingParametersDTO body) {
        var ride = Ride.builder()
                        .scheduledTime(body.getScheduledTime()) // Can be null for immediate rides
                        .status(body.getScheduledTime() != null ? RideStatus.PENDING : RideStatus.ACCEPTED)
                        .vehicleType(handleVehicleType(body))
                        .additionalServices(handleAdditionalServices(body))
                        .passengers(handlePassengers(body))
                        .rideOwner(handleOwner(userId))
                        .createdAt(LocalDateTime.now())
                        .route(handleRoute(body))
                        .build();

        if (ride.getVehicleType() != null) {
            ride.setPrice(ride.getVehicleType().getPrice());
        }

        if (ride.getScheduledTime() != null &&
                ride.getScheduledTime().isBefore(LocalDateTime.now()) &&
                ride.getScheduledTime().isAfter(LocalDateTime.now().plusHours(MAX_HOURS))
        ) {
            throw new BadRequestException("You can only schedule a ride up to " + MAX_HOURS + " hours in the future");
        }

        double estimatedDistance = 0;
        if (ride.getScheduledTime() == null) {
            // Ride is for right now and we need to look for suitable driver
            var startCoordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash()).get(0);
            var suitableDriverInfo = findBestSuitableDriver(
                    FindDriverFilter.builder()
                            .additionalServicesIds(body.getAdditionalServicesIds())
                            .vehicleTypeId(body.getVehicleTypeId())
                            .longitude(startCoordinates.getLongitude())
                            .latitude(startCoordinates.getLatitude())
                            .build()
                )
                    .orElseThrow(() -> new ResourceNotFoundException("No suitable driver found at this time with this filters"));
            ride.setDriver(driverRepository.findById(suitableDriverInfo.getDriverId()).orElseThrow(
                    () -> new ResourceNotFoundException("Driver not found")
            ));
            estimatedDistance = suitableDriverInfo.getEstimatedDistance();
        }
        rideRepository.save(ride);

        // Send emails to passengers
        applicationEventPublisher.publishEvent(new RideCreatedEvent(ride));

        return new NewRideDTO(ride.getId(), ride.getStatus().toString(), estimatedDistance);
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
        ride.setStartTime(LocalDateTime.now());
        driver.setDriverStatus(DriverStatus.BUSY);
        rideRepository.save(ride);
        driverRepository.save(driver);
        return Map.of(
                "message", "Ride started successfully",
                "ok", true,
                "rideStatus", ride.getStatus().toString(),
                "driverStatus", driver.getDriverStatus().toString()
                );
    }

    private Customer handleOwner(Long userId) {
        return customerRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer with id " + userId + " not found"
                        )
                );
    }

    private List<Passenger> handlePassengers(RideBookingParametersDTO body) {
        if (body.getPassengers() == null || body.getPassengers().isEmpty()) {
            return List.of();
        }
        List<Passenger> passengers = new ArrayList<>();
        var users = customerRepository.findByEmailIn(body.getPassengers());
        for (var passengerEmail : body.getPassengers()) {
            // Check if user exists
            var user = users.stream()
                    .filter(u -> u.getEmail().equals(passengerEmail))
                    .findFirst();
            Passenger passenger = new Passenger();
            if (user.isPresent()) {
                passenger.setUser(user.get());
            } else {
                passenger.setEmail(passengerEmail);
            }
            passenger.setAccessToken(UUID.randomUUID().toString());
            passengers.add(passenger);
        }
        passengerRepository.saveAll(passengers);
        return passengers;
    }

    private Set<AdditionalService> handleAdditionalServices(RideBookingParametersDTO body) {
        if (body.getAdditionalServicesIds() == null || body.getAdditionalServicesIds().isEmpty()) {
            return Set.of();
        }
        var additionalServices = additionalServiceRepository.findAllById(body.getAdditionalServicesIds());
        if (additionalServices.size() != body.getAdditionalServicesIds().size()) {
            throw new ResourceNotFoundException("One or more additional services not found");
        }
        return Set.copyOf(additionalServices);
    }

    private Route handleRoute(RideBookingParametersDTO body) {
        // If routeId is provided, fetch the existing route
        if (body.getRouteId() != null) {
            var existingRoute = routeRepository.findById(body.getRouteId());
            if (existingRoute.isEmpty()) {
                throw new ResourceNotFoundException("Route with id " + body.getRouteId() + " not found");
            }
            return existingRoute.get();
        }
        // Logic to create a new route based on body.getRoute()
        List<double[]> coordinates = new ArrayList<>();
        List<Location> locations = new ArrayList<>();

        for (var point : body.getRoute()) {
            // Collect coordinates for geohash transformation
            coordinates.add(new double[] {point.getLatitude(), point.getLongitude()});
            // Handle location creation
            String geohash = locationTransformer
                    .transformFromPoints(List.of(
                            new double[] {point.getLatitude(), point.getLongitude()}
                    ));
            // Avoid duplicating locations
            if (locationRepository.existsByGeoHash(geohash)) {
                continue;
            }
            locations.add(
                    Location.builder()
                        .address(point.getAddress())
                        .latitude(point.getLatitude())
                        .longitude(point.getLongitude())
                        .geoHash(geohash)
                        .build()
                );
        }

        // Create route geohash
        String routeGeohash = locationTransformer.transformFromPoints(coordinates);
        Route route = new Route(null, routeGeohash);

        // Save route and locations
        routeRepository.save(route);
        locationRepository.saveAll(locations);

        return route;
    }

    private VehicleType handleVehicleType(RideBookingParametersDTO body) {
        if (body.getVehicleTypeId() == null) {
            return null;
        }
        return vehicleTypeRepository
                .findById(body.getVehicleTypeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Vehicle type with id " + body.getVehicleTypeId() + " not found"
                        )
                );
    }

    public RideResponseDTO getActiveRideByDriverId(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver with id " + driverId + " not found"));

        Ride activeRide = rideRepository
                .findFirstByDriverAndStatusIn(
                        driver,
                        List.of(RideStatus.ACCEPTED, RideStatus.ACTIVE)
                )
                .orElse(null);

        if (activeRide == null) {
            return null;
        }

        return RideMapper.convertToHistoryResponseDTO(activeRide);
    }

    @Override
    public void endRideById(Long id, Driver driver) {
        Ride ride = rideRepository.findById(id).orElse(null);
        if(ride == null) {
            throw new ResourceNotFoundException("Ride with id " + id + " not found");
        }
        if(!ride.getDriver().getId().equals(driver.getId())) {
            throw new UnauthenticatedException("Driver not authorized to end this ride");
        }
        String path = rideTracingService.finishRoute(driver.getId());
        ride.setPath(path);
        ride.setTotalCost(ride.getTotalCost() + ride.getPrice() * locationTransformer.calculateDistanceAir(path));
        List<Coordinates>pathCords = locationTransformer.transformToCoordinates(path);
        List<Coordinates> route = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
        List<Coordinates> newCords = getNewRideCords(pathCords, route, 50);
        String newPath = locationTransformer.transformLocation(newCords);
        Route newRoute = routeRepository.findByGeoHash(newPath).orElse(null);
        if(newRoute == null) {
            newRoute = new Route();
            newRoute.setGeoHash(newPath);
            routeRepository.save(newRoute);
        }
        ride.setRoute(newRoute);
        rideRepository.save(ride);
    }
    private List<Coordinates> getNewRideCords(List<Coordinates> accualRide, List<Coordinates> plannedRoute , double thresholdMeters) {
        if (accualRide.isEmpty() || plannedRoute.isEmpty()) {
            return null;
        }

        ArrayList<Coordinates> newCords = new ArrayList<>();
        for (int i = 0; i < plannedRoute.size(); i++) {
            boolean isViewed = false;
            if(i == 0)
            {
                newCords.add(plannedRoute.get(i));
            }
            Coordinates plannedCords = plannedRoute.get(i);
            for (Coordinates actualCords: accualRide) {
                double distance =  plannedCords.distanceAirLine(actualCords);
                if (distance <= thresholdMeters) {
                    isViewed = true;
                    break;
                }
            }
            if (isViewed) {
                newCords.add(plannedCords);
            } else if(i == plannedRoute.size() -1) {
                newCords.add(accualRide.get(accualRide.size() -1)); // add last actual cord
            }
        }

        return newCords;
    }

    @Override
    public void endRideById(Long id, Driver driver) {
        Ride ride = rideRepository.findById(id).orElse(null);
        if(ride == null) {
            throw new ResourceNotFoundException("Ride with id " + id + " not found");
        }
        if(!ride.getDriver().getId().equals(driver.getId())) {
            throw new UnauthenticatedException("Driver not authorized to end this ride");
        }
        String path = rideTracingService.finishRoute(driver.getId());
        ride.setPath(path);
        ride.setTotalCost(ride.getTotalCost() + ride.getPrice() * locationTransformer.calculateDistanceAir(path));
        List<Coordinates>pathCords = locationTransformer.transformToCoordinates(path);
        List<Coordinates> route = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
        List<Coordinates> newCords = getNewRideCords(pathCords, route, 50);
        String newPath = locationTransformer.transformLocation(newCords);
        Route newRoute = routeRepository.findByGeoHash(newPath).orElse(null);
        if(newRoute == null) {
            newRoute = new Route();
            newRoute.setGeoHash(newPath);
            routeRepository.save(newRoute);
        }
        ride.setRoute(newRoute);
        rideRepository.save(ride);
    }
    private List<Coordinates> getNewRideCords(List<Coordinates> accualRide, List<Coordinates> plannedRoute , double thresholdMeters) {
        if (accualRide.isEmpty() || plannedRoute.isEmpty()) {
            return null;
        }

        ArrayList<Coordinates> newCords = new ArrayList<>();
        for (int i = 0; i < plannedRoute.size(); i++) {
            boolean isViewed = false;
            if(i == 0)
            {
                newCords.add(plannedRoute.get(i));
            }
            Coordinates plannedCords = plannedRoute.get(i);
            for (Coordinates actualCords: accualRide) {
                double distance =  plannedCords.distanceAirLine(actualCords);
                if (distance <= thresholdMeters) {
                    isViewed = true;
                    break;
                }
            }
            if (isViewed) {
                newCords.add(plannedCords);
            } else if(i == plannedRoute.size() -1) {
                newCords.add(accualRide.get(accualRide.size() -1)); // add last actual cord
            }
        }

        return newCords;
    }

    public Optional<FindDriverDTO> findBestSuitableDriver(FindDriverFilter filter) {
        Map<Long, DriverInformation> drivers = new HashMap<>();
        Set<Long> checkedIds = new HashSet<>();
        Set<Long> driversIds = new HashSet<>();
        for (var KM : KM_RANGE) {
            driversIds.clear();
            List<RedisLocationsDTO> driversLocations = getRedisLocationsDTOS(
                    filter.getLatitude(),
                    filter.getLongitude(),
                    KM
            );
            // No drivers nearby, look further
            if (driversLocations.isEmpty()) {
                continue;
            }
            mapLocationsToDriversInformation(driversLocations, checkedIds, driversIds, drivers);

            getVehicleData(filter, driversIds, drivers);
            // No more driver's continue further
            if (drivers.isEmpty()) {
                continue;
            }

                getRideData(driversIds, drivers);
            // No more driver's continue further
            if (drivers.isEmpty()) {
                continue;
            }

            // There are some suitable driver, find the closest one
            var closestInfo = getClosestDriver(filter, drivers);
            return Optional.of(new FindDriverDTO(closestInfo.getId(), closestInfo.getTotalDistance()));
        }
        // No suitable driver found
        return Optional.empty();
    }

    private DriverInformation getClosestDriver(FindDriverFilter filter, Map<Long, DriverInformation> drivers) {
        DriverInformation closest = null;
        for (var driver : drivers.values()) {
            var driverCoordinates = coordinatesFactory.getCoordinate(driver.getLatitude(), driver.getLongitude());
            var startCoordinates = coordinatesFactory.getCoordinate(filter.getLatitude(), filter.getLongitude());
            if (driver.getCurrentRide() != null) {
                var routeCoordinates = locationTransformer.transformToCoordinates(driver.getCurrentRide().getRoute().getGeoHash());
                var routeEndCoordinate = routeCoordinates.get(routeCoordinates.size() - 1);
                driver.setRideEndDistance(driverCoordinates.distanceAirLine(routeEndCoordinate));
                driver.setToStartPointDistance(routeEndCoordinate.distanceAirLine(startCoordinates));
            } else {
                driver.setToStartPointDistance(driverCoordinates.distanceAirLine(startCoordinates));
            }
            if (closest == null || closest.getTotalDistance() > driver.getTotalDistance()) {
                closest = driver;
            }
        }
        return closest;
    }

    private void getRideData(Set<Long> driversIds, Map<Long, DriverInformation> drivers) {
        List<Ride> rides = rideRepository.findByDriverIdInAndEndTimeIsNullOrderByCreatedAtAsc(driversIds);
        for (var ride : rides) {
            var driverInfo = drivers.get(ride.getDriver().getId());
            // If the ride is already set it means driver already has next ride so he is not suitable
            if (driverInfo.getCurrentRide() != null) {
                drivers.remove(ride.getDriver().getId());
                driversIds.remove(ride.getDriver().getId());
                continue;
            }
            driverInfo.setCurrentRide(ride);
        }
    }

    private void getVehicleData(FindDriverFilter filter, Set<Long> driversIds, Map<Long, DriverInformation> drivers) {
        var vehicles = vehicleRepository.findByDriverIdIn(driversIds);
        for (var vehicle : vehicles) {
            if (!doesVehicleMatch(vehicle, filter)) {
                drivers.remove(vehicle.getDriver().getId());
                driversIds.remove(vehicle.getDriver().getId());
                continue;
            }
            drivers.get(vehicle.getDriver().getId()).setVehicle(vehicle);
        }
    }

    private static void mapLocationsToDriversInformation(
            List<RedisLocationsDTO> driversLocations,
            Set<Long> checkedIds,
            Set<Long> driversIds,
            Map<Long, DriverInformation> drivers
    ) {
        for (var location : driversLocations) {
            if (checkedIds.contains(location.getId())) {
                continue;
            }
            checkedIds.add(location.getId());
            driversIds.add(location.getId());
            drivers.put(
                    location.getId(),
                    DriverInformation.builder()
                            .id(location.getId())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build()
                );
        }
    }

    private List<RedisLocationsDTO> getRedisLocationsDTOS(double latitude, double longitude, Integer KM) {
        if (KM > 0) {
            return driverLocationsRepository
                    .getLocationsWithinRadius(
                            longitude,
                            latitude,
                            KM
                    );
        }
        else {
            return driverLocationsRepository.getAllLocations();
        }
    }

    private boolean doesVehicleMatch(Vehicle vehicle, FindDriverFilter filter) {
        // Check if vehicle type is set and matches
        if (filter.getVehicleTypeId() != null &&
                !Objects.equals(vehicle.getVehicleType().getId(), filter.getVehicleTypeId())) {
            return false;
        }
        // Check if vehicle has all necessary additional services
        if (filter.getAdditionalServicesIds() != null && !filter.getAdditionalServicesIds().isEmpty()) {
            return new HashSet<Long>(
                        vehicle
                                .getAdditionalServices()
                                .stream()
                                .map(AdditionalService::getId)
                                .toList()
                    )
                    .containsAll(filter.getAdditionalServicesIds());
        }
        return true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class DriverInformation {
        Long id;
        double latitude;
        double longitude;
        Vehicle vehicle;
        Ride currentRide;
        double rideEndDistance = 0;
        double toStartPointDistance = 0;
        public double getTotalDistance() {
            return rideEndDistance + toStartPointDistance;
        }
    }
}
