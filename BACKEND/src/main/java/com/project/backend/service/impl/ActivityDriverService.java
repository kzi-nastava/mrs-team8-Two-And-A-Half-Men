package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.RideCancelationDTO;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.models.Driver;
import com.project.backend.models.DriverActivity;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.DriverStatus;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.DriverActivityRepository;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IActivityDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityDriverService implements IActivityDriver {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverActivityRepository driverActivityRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private CancellationService cancellationService;
    @Override
    public boolean isTakingWork(Driver driver) {
        if(driver.getDriverStatus() == DriverStatus.INACTIVE) return false;
        if(workingHours(driver , 8)) {
            deActivateDriver(driver);
            return false;
        }
        if(driver.getDriverStatus() == DriverStatus.ACTIVE || driver.getDriverStatus() == DriverStatus.BUSY) return true;
        return false;
    }

    @Override
    public boolean workingHours(Driver driver, int hours) {
        List<DriverActivity> activities = driverActivityRepository.findByDriverAndStartTimeAfter(driver, LocalDateTime.now().minusDays(1));
        int totalHours = activities.stream()
                .mapToInt(activity -> (int) Duration.between(activity.getStartTime(), activity.getEndTime() != null ? activity.getEndTime() : LocalDateTime.now()).toHours())
                .sum();
        return totalHours >= hours;
    }

    @Override
    public void deActivateDriver(Driver driver) {
        if(rideRepository.findFirstByDriverAndStatusIn(driver, List.of(RideStatus.ACTIVE)).isPresent()) throw new IllegalStateException("Driver has active rides and cannot be deactivated.");
        driver.setDriverStatus(DriverStatus.INACTIVE);
        driverRepository.save(driver);
        cancelRides(driver);
        List<DriverActivity> activities = driverActivityRepository.findByDriverAndEndTimeIsNull(driver);
        for(DriverActivity activity : activities) {
                activity.setEndTime(LocalDateTime.now());
                driverActivityRepository.save(activity);
            }

    }

    @Override
    public void activateDriver(Driver driver) {
        if(driver.getDriverStatus() != DriverStatus.INACTIVE || workingHours(driver, 8))
            throw new BadRequestException("Driver is not eligible for activation");
        if(isDriving(driver)) {
            driver.setDriverStatus(DriverStatus.ACTIVE);
            driverRepository.save(driver);
            return;
        }
        DriverActivity activity = new DriverActivity();
        activity.setDriver(driver);
        activity.setStartTime(LocalDateTime.now());
        driverActivityRepository.save(activity);
        driver.setDriverStatus(DriverStatus.ACTIVE);
        driverRepository.save(driver);
    }

    @Override
    public void cancelRides(Driver driver) {
      List<Ride> rides = rideRepository.findByDriverAndStatusIn(driver, List.of(RideStatus.ACCEPTED));
      if(rides.isEmpty()) return;
      for(Ride ride : rides) {
          RideCancelationDTO cancelationDTO = new RideCancelationDTO();
          cancelationDTO.setCancelledBy("DRIVER");
          cancelationDTO.setReason("Driver cant continue working");
          cancellationService.cancelRide(ride.getId(), cancelationDTO, driver);
      }
    }
    @Override
    public boolean isDriving(Driver driver) {
        List<DriverActivity> activities =  driverActivityRepository.findByDriverAndEndTimeIsNull(driver);
        if(activities.isEmpty()) return false;
        return true;
    }

}
