package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.RideCancelationDTO;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.ICancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CancellationService implements ICancellationService {

    @Autowired
    private RideRepository rideRepository;
    @Override
    public void cancelRide(Long rideId, RideCancelationDTO reason, AppUser user) {
        if(user instanceof Customer) {
            Customer customer = (Customer) user;
            Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
            if(!ride.getRideOwner().getId().equals(customer.getId())) {
                throw new UnauthorizedException("You are not authorized to cancel this ride");
            }
            if((ride.getStatus() == RideStatus.ACCEPTED || ride.getStatus() == RideStatus.PENDING) && ride.getScheduledTime().isAfter(LocalDateTime.now().plusMinutes(10))) {
                ride.setStatus(RideStatus.CANCELLED);
                ride.setCancellationReason(null);
                rideRepository.save(ride);
                //Notify driver if assigned

            } else {
                throw new IllegalArgumentException("Ride cannot be cancelled at this stage");
            }
        }
        if(user instanceof Driver) {
            // Driver cancellation logic
            // Cancel ride if its customer issues leave it canceled and
            // Move on if its driver issues than rebook the ride
            Driver driver = (Driver) user;
            Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
            if(!ride.getDriver().getId().equals(driver.getId())) {
                throw new UnauthorizedException("You are not authorized to cancel this ride");
            }
            if(reason.getCancelledBy().equals("CUSTOMER")) {
                ride.setStatus(RideStatus.CANCELLED);
                ride.setCancellationReason(reason.getReason());
                rideRepository.save(ride);
                // Rider is free and can go and drive more with no issues
            } else if(reason.getCancelledBy().equals("DRIVER")) {
                // Rebook logic here
                ride.setStatus(RideStatus.PENDING);
                ride.setDriver(null);
                rideRepository.save(ride);
                // Start new rebook process here
                // Creates a dummy object if needed with same data without passangers

            } else {
                throw new IllegalArgumentException("Invalid cancellation initiator");
            }

        }

    }
}
