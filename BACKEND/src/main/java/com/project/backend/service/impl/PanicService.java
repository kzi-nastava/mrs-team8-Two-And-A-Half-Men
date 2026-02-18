package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.*;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.PassengerRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.repositories.redis.DriverLocationsRepository;
import com.project.backend.service.DateTimeService;
import com.project.backend.service.IPanicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PanicService implements IPanicService {

    private final PassengerRepository passengerRepository;
    private final RideRepository rideRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DriverLocationsRepository driverLocationsRepository;
    private final DateTimeService dateTimeService;
    private final RideMapper rideMapper;
    private final RideTracingService rideTracingService;
    private final DriverRepository driverRepository;
    private final ActivityDriverService driverActivityService;


    public void triggerPanicAlert(AppUser user, String accessToken) {
        System.out.println("Panic alert triggered");
        Passenger passenger = null;
        if (accessToken != null) {
            passenger = passengerRepository.findByAccessToken(accessToken).orElse(null);
        }
        System.out.println(passenger);
        if(passenger == null && user instanceof Customer) {
            passenger = passengerRepository.findByCustomerWithRideStatus(user,
                    List.of(RideStatus.ACTIVE)).orElse(null);
        } else if(passenger == null && user instanceof Driver driver) {
            System.out.println("Driver triggering panic");
            Ride activeRide = rideRepository.findRideOfDriverWithStatus(driver, List.of(RideStatus.ACTIVE))
                    .orElse(null);
            if(activeRide == null) {
                throw new IllegalArgumentException("No active ride found for the driver.");
            }
            driver.setDriverStatus(DriverStatus.PANICKING);
            activeRide.setStatus(RideStatus.PANICKED);
            rideRepository.save(activeRide);
            driverActivityService.cancelRides(driver);
            driverRepository.save(driver);
            String driverLocation = "Unknown";
            // driverLocationsRepository.setLocations(driver.getId(), 10 , 10) ; // Mock location
            try {
                Point driverPoint = driverLocationsRepository.getLocation(driver.getId());
                driverLocation = driverPoint.getX() + "," + driverPoint.getY();
            } catch (Exception e) {
                System.out.println("Could not retrieve driver location: " + e.getMessage());
            }
            Map<String, String> panicAlert = Map.of(
                    "driverName", driver.getEmail(),
                    "driverLocation", driverLocation,
                    "rideId", activeRide.getId().toString()
            );
            System.out.println("Sending panic alert to admins: " + panicAlert);
            simpMessagingTemplate.convertAndSend("/topic/panic", panicAlert);
            return;
        }
        System.out.println(passenger);
        if(passenger == null || passenger.getRide() == null) {
                throw new IllegalArgumentException("No active ride found for the passenger.");
        }
        passenger.getRide().setStatus(RideStatus.PANICKED);
        rideRepository.save(passenger.getRide());
        Driver driver = passenger.getRide().getDriver();
        driver.setDriverStatus(DriverStatus.PANICKING);
        driverRepository.save(driver);
        driverActivityService.cancelRides(driver);
        String driverLocation = "Unknown";
        driverLocationsRepository.setLocations(driver.getId(), 10 , 10) ; // Mock location
        try {
            Point driverPoint = driverLocationsRepository.getLocation(driver.getId());
            driverLocation = driverPoint.getX() + "," + driverPoint.getY();
        } catch (Exception e) {
            System.out.println("Could not retrieve driver location: " + e.getMessage());
        }
        Map<String, String> panicAlert = Map.of(
                "passengerName", passenger.getUser() != null ? passenger.getUser().getEmail() : passenger.getEmail(),
                "driverName", driver.getEmail(),
                "driverLocation", driverLocation,
                "rideId", passenger.getRide().getId().toString()
        );
        System.out.println("Sending panic alert to admins: " + panicAlert);
        simpMessagingTemplate.convertAndSend("/topic/panic", panicAlert);
        System.out.println("Panic alert sent to admins.");
    }
    public void resolvePanicAlert(Long rideId)
    {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride with id " + rideId + " not found"));
        if (ride.getStatus() != RideStatus.PANICKED) {
            throw new IllegalStateException("Ride is not in panicked state");
        }
        if(ride.getEndTime() != null) {
            throw new IllegalStateException("Ride has already ended");
        }
        ride.setEndTime(dateTimeService.getCurrentDateTime());
        ride.setPath(null); // Clear the path to prevent further tracking
        rideTracingService.finishRoute(ride.getDriver().getId());
        Driver driver = ride.getDriver();
        driverActivityService.deActivateDriver(driver);
        driverRepository.save(driver);
        rideRepository.save(ride);
    }
    public List<RideResponseDTO> getAllUnresolvedPanicAlerts() {
        List<Ride> panickedRides = rideRepository.findByStatusAndEndTimeIsNull(RideStatus.PANICKED);
        return  panickedRides.stream()
                .map(rideMapper::convertToRideResponseDTO)
                .toList();
    }
}
