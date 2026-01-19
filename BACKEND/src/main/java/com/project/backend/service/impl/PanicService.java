package com.project.backend.service.impl;

import com.project.backend.models.*;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.PassengerRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IPanicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class PanicService implements IPanicService {

    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void triggerPanicAlert(AppUser user, String accesToken) {
        System.out.println("Panic alert triggered");
        Passenger passenger = passengerRepository.findByAccessToken(accesToken).orElse(null);
        System.out.println(passenger);
        if(passenger == null && user instanceof Customer) {
            passenger = passengerRepository.findByCustomerWithRideStatus((Customer) user,
                    List.of(RideStatus.ACTIVE)).orElse(null);
        } else if(passenger == null && user instanceof Driver) {
            Driver driver = (Driver) user;
            System.out.println("Driver triggering panic");
            Ride activeRide = rideRepository.findRideOfDriverWithStatus(driver, List.of(RideStatus.ACTIVE))
                    .orElse(null);
            if(activeRide == null) {
                throw new IllegalArgumentException("No active ride found for the driver.");
            }
            activeRide.setStatus(RideStatus.PANICED);
            rideRepository.save(activeRide);
            String driverLocation = "45.2671° N, 19.8335° E"; // Mock location (Novi Sad coordinates)
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
        passenger.getRide().setStatus(RideStatus.PANICED);
        rideRepository.save(passenger.getRide());
        Driver driver = passenger.getRide().getDriver();
        String driverLocation = "45.2671° N, 19.8335° E"; // Mock location (Novi Sad coordinates)
        Map<String, String> panicAlert = Map.of(
                "passengerName", passenger.getUser().getEmail(),
                "driverName", driver.getEmail(),
                "driverLocation", driverLocation,
                "rideId", passenger.getRide().getId().toString()
        );
        System.out.println("Sending panic alert to admins: " + panicAlert);
        simpMessagingTemplate.convertAndSend("/topic/panic", panicAlert);

        System.out.println("Panic alert sent to admins.");
    }
}
