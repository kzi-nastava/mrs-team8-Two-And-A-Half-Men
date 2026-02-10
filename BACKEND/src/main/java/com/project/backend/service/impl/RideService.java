package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.*;
import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.DTO.mappers.RideMapper;
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
import com.project.backend.service.DriverMatchingService;
import com.project.backend.service.IRideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final RideMapper rideMapper;

    private final SimpMessagingTemplate messagingTemplate;

    private final ResolvePassengerService resolvePassengerService;

    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        return rideMapper.convertToRideResponseDTO(ride);
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

        return rideMapper.convertToRideResponseDTO(activeRide);
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

        return rideMapper.convertToRideResponseDTO(activeRide);
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
    private List<Coordinates> getNewRideCords(List<Coordinates> actualRide, List<Coordinates> plannedRoute , double thresholdMeters) {
            if (actualRide.isEmpty() || plannedRoute.isEmpty()) {
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
                for (Coordinates actualCords: actualRide) {
                    double distance =  plannedCords.distanceAirLine(actualCords);
                    if (distance <= thresholdMeters) {
                        isViewed = true;
                        break;
                    }
                }
                if (isViewed) {
                    newCords.add(plannedCords);
                } else if(i == plannedRoute.size() -1) {
                    newCords.add(actualRide.get(actualRide.size() -1)); // add last actual cord
                }
            }

            return newCords;
    }


    @Override
    public CostTimeDTO estimateRide(RideBookingParametersDTO rideData) {
        FindDriverDTO driver = driverMatchingService.findBestDriver(
                FindDriverFilter.builder()
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
    public List<RideBookedDTO> getAllBookedRidesByCustomer(Customer customer) {
        List<RideBookedDTO> bookedRides = new ArrayList<>();
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
                String ScheduleTime = ride.getScheduledTime() != null ? ride.getScheduledTime().toString() : "Immediate";
                String driverName = ride.getDriver() != null ? ride.getDriver().firstNameAndLastName() : "Not assigned";
                RideBookedDTO rideBookedDTO = RideBookedDTO.builder()
                        .id(ride.getId())
                        .status(ride.getStatus().toString())
                        .scheduleTime(ScheduleTime)
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
                .map(rideMapper::convertToRideResponseDTO)
                .toList();

        return PagedResponse.fromPage(content, rides);
    }
}
