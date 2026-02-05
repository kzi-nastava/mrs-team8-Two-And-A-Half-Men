package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.RideCancelationDTO;
import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.ICancellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CancellationService implements ICancellationService {

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private LocationTransformer locationTransformer;
    @Autowired
    private RideService rideService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverLocationService driverLocationService;
    @Override
    public void cancelRide(Long rideId, RideCancelationDTO reason, AppUser user) {
        if(user instanceof Customer) {
            CustomerCancelRide(rideId, user);
            return;
        }
        if(user instanceof Driver) {
            DriverCancelRide(rideId, user, reason);
        }
    }
    private void CustomerCancelRide(Long rideId, AppUser user) {
        Customer customer = (Customer) user;
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        if(!ride.getRideOwner().getId().equals(customer.getId())) {
            throw new ForbiddenException("You are not authorized to cancel this ride");
        }
        if((ride.getStatus() == RideStatus.ACCEPTED || ride.getStatus() == RideStatus.PENDING) && ride.getScheduledTime() != null &&ride.getScheduledTime().isAfter(LocalDateTime.now().plusMinutes(10))) {
            ride.setStatus(RideStatus.CANCELLED);
            ride.setCancellationReason(null);
            rideRepository.save(ride);

        } else {
            throw new BadRequestException("Ride cannot be cancelled at this stage");
        }
    }
    private void DriverCancelRide(Long rideId, AppUser user, RideCancelationDTO reason) {
        Driver driver = (Driver) user;
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new BadRequestException("Ride not found"));
        if(!ride.getDriver().getId().equals(driver.getId())) {
            throw new ForbiddenException("You are not authorized to cancel this ride");
        }
        if(!(ride.getStatus() == RideStatus.ACCEPTED || ride.getStatus() == RideStatus.PENDING)) {
            throw new BadRequestException("Cannot cancel this ride");
        }
        if(reason.getCancelledBy().equals("CUSTOMER")) {
            ride.setStatus(RideStatus.CANCELLED);
            ride.setCancellationReason(reason.getReason());
            rideRepository.save(ride);
        } else if(reason.getCancelledBy().equals("DRIVER")) {
            FindDriverFilter filter = new FindDriverFilter();
            List<Long> additionalServicesIds = ride.getAdditionalServices().stream().map(service -> service.getId()).toList();
            filter.setAdditionalServicesIds(additionalServicesIds);
            if(ride.getVehicleType() == null) {
                filter.setVehicleTypeId(null);
            } else {
                filter.setVehicleTypeId(ride.getVehicleType().getId());
            }
            String route = ride.getRoute().getGeoHash();
            List<Coordinates> coordinates = locationTransformer.transformToCoordinates(route);
            filter.setLatitude(coordinates.get(0).getLatitude());
            filter.setLongitude(coordinates.get(0).getLongitude());
            driver.setDriverStatus(DriverStatus.INACTIVE);
            driverLocationService.removeDriverLocation(driver.getId());
            driverRepository.save(driver);
            FindDriverDTO foundDriver = rideService.findBestSuitableDriver(filter).orElseThrow(() -> new ResourceNotFoundException("No suitable driver found"));
            System.out.println("Found driver for reassignment: " + foundDriver.getDriverId());
            Driver newDriver = driverRepository.findById(foundDriver.getDriverId()).orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            ride.setDriver(newDriver);
            rideRepository.save(ride);
            //NOt sure what this mean ? but ok go on
        } else {
            throw new BadRequestException("Invalid cancellation initiator");
        }
    }
}
