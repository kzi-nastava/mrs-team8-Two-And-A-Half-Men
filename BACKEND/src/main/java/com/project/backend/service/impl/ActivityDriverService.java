package com.project.backend.service.impl;

import com.project.backend.models.Driver;
import com.project.backend.models.DriverActivity;
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
    @Override
    public boolean isTakingWork(Driver driver) {
        if(driver.getDriverStatus() == DriverStatus.INACTIVE) return false;
        if(workingHours(driver , 8)) {
            deActivateDriver(driver);
            return false;
        }
        return true;
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
        driver.setDriverStatus(DriverStatus.INACTIVE);
        driverRepository.save(driver);
        lastRide(driver);
    }

    @Override
    public void activateDriver(Driver driver) {
        if(driver.getDriverStatus() != DriverStatus.INACTIVE || workingHours(driver, 8))
            return;
        if(isDriving(driver)) {
            driver.setDriverStatus(DriverStatus.ACTIVE);
            driverRepository.save(driver);
        }


    }

    @Override
    public void lastRide(Driver driver) {
        if(!rideRepository.findFirstByDriverAndStatusIn(driver, List.of(RideStatus.ACTIVE , RideStatus.ACCEPTED, RideStatus.PENDING)).isPresent()) {
            List<DriverActivity> activity = driverActivityRepository.findByDriverAndEndTimeIsNull(driver);
            for (DriverActivity driverActivity : activity) {
                driverActivity.setEndTime(LocalDateTime.now());
                driverActivityRepository.save(driverActivity);
            }
        }
    }
    @Override
    public boolean isDriving(Driver driver) {
        List<DriverActivity> activities =  driverActivityRepository.findByDriverAndEndTimeIsNull(driver);
        if(activities.isEmpty()) return false;
        return true;
    }
}
