package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.*;
import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.DTO.redis.RedisLocationsDTO;
import com.project.backend.events.RideCreatedEvent;
import com.project.backend.events.RideFinishedEvent;
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
import com.project.backend.repositories.redis.DriverLocationsRepository;
import com.project.backend.service.IRideService;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RideService implements IRideService {

    private final DriverLocationsRepository driverLocationsRepository;
    private final CoordinatesFactory coordinatesFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AppUserRepository appUserRepository;
    @Value("${ride-booking.max-hours}")
    private int MAX_HOURS;

    private final List<Integer> KM_RANGE = List.of(30, 50, 70, 100, -1);

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RideTracingService rideTracingService;
    private final LocationTransformer locationTransformer;
    private final CustomerRepository customerRepository;
    private final PassengerRepository passengerRepository;
    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final ResolvePassengerService resolvePassengerService;

    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        return RideMapper.convertToRideResponseDTO(ride);
    }

    @Transactional
    @Override
    public NewRideDTO createRide(Long userId, RideBookingParametersDTO body) {
        var rideOwner = appUserRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        var ride = Ride.builder()
                        .scheduledTime(body.getScheduledTime()) // Can be null for immediate rides
                        .status(body.getScheduledTime() != null ? RideStatus.PENDING : RideStatus.ACCEPTED)
                        .vehicleType(handleVehicleType(body))
                        .additionalServices(handleAdditionalServices(body))
                        .passengers(handlePassengers(body, rideOwner))
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

        linkRideToPassengers(ride);

        // Send emails to passengers
        applicationEventPublisher.publishEvent(new RideCreatedEvent(ride));

        return new NewRideDTO(ride.getId(), ride.getStatus().toString(), estimatedDistance);
    }

    private void linkRideToPassengers(Ride ride) {
        for (var passenger : ride.getPassengers()) {
            passenger.setRide(ride);
        }
        passengerRepository.saveAll(ride.getPassengers());
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

    private List<Passenger> handlePassengers(RideBookingParametersDTO body, AppUser rideOwner) {
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
        passengers.add(
                Passenger.builder()
                        .user((Customer)rideOwner)
                        .accessToken(UUID.randomUUID().toString())
                        .build()
            );
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

    public RideResponseDTO getActiveRideByDriverId(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver with id " + id + " not found"));

        Ride activeRide = rideRepository
                .findFirstByDriverAndStatusIn(driver, List.of(RideStatus.ACCEPTED, RideStatus.ACTIVE))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active ride for driver with id " + id + " not found"
                        ));

        return RideMapper.convertToRideResponseDTO(activeRide);
    }

    public RideResponseDTO getActiveRideByCustomerId(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id " + id + " not found"));

        Ride activeRide = rideRepository
                .findFirstByRideOwnerAndStatusIn(customer, List.of(RideStatus.ACTIVE))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active ride for customer with id " + id + " not found"
                        ));

        return RideMapper.convertToRideResponseDTO(activeRide);
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
        if (noteText.isEmpty() || noteText.isBlank() || noteText.length() > 500)
            throw new BadRequestException("Note text length must be between 1 and 500 characters");

        passenger.setInconsistencyNote(noteText);
        passengerRepository.save(passenger);

        return NoteResponseDTO.builder()
                .noteText(noteRequest.getNoteText())
                .passengerMail(passenger.getEmail())
                .rideId(ride.getId())
                .build();
    }

    public RideTrackingDTO getRideTrackingInfo(PassengerActor actor) {
        Passenger passenger = resolvePassengerService.resolveActorOnActiveRide(actor);

        Long rideId = passenger.getRide().getId();
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride with id " + rideId + " not found"));

        List<Coordinates> coordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
        List<String> hashes = coordinates
                .stream().map(
                        c -> locationTransformer
                                .transformFromPoints(List.of(new double[] {c.getLatitude(), c.getLongitude()}))
                ).toList();
        var locations = locationRepository.findAllByGeoHashIn(hashes);

        return RideTrackingDTO.builder()
                .id(ride.getId())
                .driverId(ride.getDriver().getId())
                .stops(locations)
                .status(ride.getStatus())
                .startTime(ride.getStartTime())
                .build();
    }

    public void sendRideUpdate(Ride ride) {
        RideTrackingDTO rideTrackingDTO = RideTrackingDTO.builder()
                .id(ride.getId())
                .status(ride.getStatus())
                .startTime(ride.getStartTime())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/rides/" + ride.getId(),
                rideTrackingDTO
        );
    }

    @Override
    public CostTimeDTO endRideById(Long id, Driver driver) {
        System.out.println("Ride end requested for ride id: " + id + " by driver id: " + driver.getId());
        Ride ride = rideRepository.findById(id).orElse(null);

        if(ride == null) {
            throw new BadRequestException("Ride with id " + id + " not found");
        }
        System.out.println("Ending ride for driver id: " + driver.getId() + ", ride id: " + ride.getId());
        if(!ride.getDriver().getId().equals(driver.getId())) {
            throw new ForbiddenException("Driver not authorized to end this ride");
        }
        System.out.println("Ride status: " + ride.getStatus());
        if(ride.getStatus() != RideStatus.ACTIVE) {
            throw new NoActiveRideException("Ride is not active");
        }
        String path = rideTracingService.finishRoute(driver.getId());
        System.out.println(path);
        ride.setPath(path);
        if(ride.getPrice() == null) {
            ride.setPrice(0.0);
        }
        var distance = locationTransformer.calculateDistanceAir(path, MetricsDistance.KILOMETERS);
        ride.setTotalCost(ride.getPrice() + 120 * distance);
        ride.setDistanceKm(distance);
        ride.setStatus(RideStatus.FINISHED);
        /*
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
        */

        ride.setEndTime(LocalDateTime.now());
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
                    continue;
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
            return new HashSet<>(
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

    @Override
    public CostTimeDTO estimateRide(RideBookingParametersDTO rideData) {
        FindDriverDTO driver = findBestSuitableDriver(
                FindDriverFilter.builder()
                        .additionalServicesIds(rideData.getAdditionalServicesIds())
                        .vehicleTypeId(rideData.getVehicleTypeId())
                        .latitude(rideData.getRoute().get(0).getLatitude())
                        .longitude(rideData.getRoute().get(0).getLongitude())
                        .build()
        ).orElseThrow(() -> new ResourceNotFoundException("No suitable driver found for the given parameters"));
        double distance = driver.getEstimatedDistance();
        System.out.println("Distance to driver: " + distance);
        var cordinates = coordinatesFactory.getCoordinate(
                rideData.getRoute().get(0).getLatitude(),
                rideData.getRoute().get(0).getLongitude()
        );
        var destCordinates = coordinatesFactory.getCoordinate(
                rideData.getRoute().get(rideData.getRoute().size() - 1).getLatitude(),
                rideData.getRoute().get(rideData.getRoute().size() - 1).getLongitude()
        );
        double rideDistance = cordinates.distanceAirLine(destCordinates);
        double estimatedTime = MetricsDistance.KILOMETERS.fromMeters(rideDistance + distance) / 50 * 60;
        return new CostTimeDTO(
                0,
                estimatedTime
        );
    }

    @Override
    public List<RideBookedDTO> getAllBookedRidesByCustomer(Customer customer) {
        List<RideBookedDTO> bookedRides = new ArrayList<>();
        List<Ride> rides = rideRepository.findByRideOwner(customer);
        for(Ride ride : rides) {
            boolean isSheculedforNextTenMinutes = ride.getScheduledTime() != null &&
                    ride.getScheduledTime().isAfter(LocalDateTime.now().plusMinutes(10));
            if ((ride.getStatus() == RideStatus.ACCEPTED  || ride.getStatus() == RideStatus.ACTIVE || isSheculedforNextTenMinutes) && ride.getStatus() != RideStatus.CANCELLED ) {
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
                String SheduleTime = ride.getScheduledTime() != null ? ride.getScheduledTime().toString() : "Immediate";
                String driverName = ride.getDriver() != null ? ride.getDriver().firstNameAndLastName() : "Not assigned";
                RideBookedDTO rideBookedDTO = RideBookedDTO.builder()
                        .id(ride.getId())
                        .status(ride.getStatus().toString())
                        .scheduleTime(SheduleTime)
                        .driverName(driverName)
                        .route(address.toString().trim())
                        .build();
                bookedRides.add(rideBookedDTO);
            }
        }
        return bookedRides;
    }

    public RideTrackingDTO getDriversActiveRide(Driver driver) {
        Ride ride = rideRepository.findFirstByDriverAndStatusIn(
                driver, List.of(RideStatus.ACTIVE)
        ).orElseThrow(() ->
                new ResourceNotFoundException("Drivers active ride is not found")
        );

        return RideTrackingDTO.builder()
                .id(ride.getId())
                .driverId(driver.getId())
                .passengerId(null)
                .stops(null)
                .startTime(ride.getStartTime())
                .build();
    }

    @Transactional
    public void finishRide(Long id, FinishRideDTO finishRideDTO) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride with id " + id + " not found")
                );

        ride.setStatus(finishRideDTO.isInterrupted() ? RideStatus.INTERRUPTED : RideStatus.FINISHED);
        ride.getDriver().setDriverStatus(DriverStatus.ACTIVE);

        applicationEventPublisher.publishEvent(new RideFinishedEvent(ride));

        rideRepository.save(ride);
    }

    @Override
    public RideTrackingDTO getRideTrackingById(Long id, AppUser user) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride with id " + id + " not found")
                );
        if(ride.getStatus() != RideStatus.ACCEPTED && ride.getStatus() != RideStatus.ACTIVE && ride.getStatus() != RideStatus.PENDING) {
            throw new BadRequestException("Ride is not trackable");
        }
        if (user instanceof Driver) {
            if(!ride.getDriver().getId().equals(user.getId())) {
                throw new ForbiddenException("You are not assigned to this ride");
            }
        } else if (user instanceof Customer) {
            if(!ride.getRideOwner().getId().equals(user.getId())) {
                throw new ForbiddenException("You are not the owner of this ride");
            }
        }
        List<Coordinates> coordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
        List<String> hashes = coordinates
                .stream().map(
                        c -> locationTransformer
                                .transformFromPoints(List.of(new double[] {c.getLatitude(), c.getLongitude()}))
                ).toList();
        var locations = locationRepository.findAllByGeoHashIn(hashes);

        return RideTrackingDTO.builder()
                .id(ride.getId())
                .driverId(ride.getDriver() != null ? ride.getDriver().getId() : null)
                .stops(locations)
                .status(ride.getStatus())
                .startTime(ride.getStartTime() != null ? ride.getStartTime() : ride.getScheduledTime() != null ? ride.getScheduledTime() : null)
                .build();
    }
    public PagedResponse<RideResponseDTO> getActiveRides(
            Pageable pageable,
            String driverFirstName,
            String driverLastName
    ) {
        Page<Ride> rides = rideRepository.findActiveRides(
                List.of(RideStatus.ACTIVE),
                driverFirstName,
                driverLastName,
                pageable
        );

        List<RideResponseDTO> content = rides.getContent()
                .stream()
                .map(RideMapper::convertToRideResponseDTO)
                .toList();

        return PagedResponse.fromPage(content, rides);
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
