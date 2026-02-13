package com.project.backend.service.impl;

import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.*;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
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
            activeRide.setStatus(RideStatus.PANICKED);
            rideRepository.save(activeRide);
            // driverLocationsRepository.setLocations(driver.getId(), 10 , 10) ; // Mock location
            Point driverPoint = driverLocationsRepository.getLocation(driver.getId());
            String driverLocation = driverPoint.getX() + "," + driverPoint.getY();
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
        driverLocationsRepository.setLocations(driver.getId(), 10 , 10) ; // Mock location
        Point driverPoint = driverLocationsRepository.getLocation(driver.getId());
        String driverLocation = driverPoint.getX() + "," + driverPoint.getY();
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

    public void panic(Long rideId, Long userId, String accessToken) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new IllegalStateException("Panic can only be triggered for active rides.");
        }

        // Verify users

        // Update the ride status to PANICKED
        ride.setStatus(RideStatus.PANICKED);
        ride.setEndTime(dateTimeService.getCurrentDateTime());
        ride.getDriver().setDriverStatus(DriverStatus.ACTIVE);
        rideRepository.save(ride);

        // Notify admins about the panic alert
        Map<String, String> panicAlert = Map.of(
                "userId", userId.toString(),
                "rideId", rideId.toString(),
                "accessToken", accessToken
        );
        simpMessagingTemplate.convertAndSend("/topic/panic", panicAlert);
    }
}
