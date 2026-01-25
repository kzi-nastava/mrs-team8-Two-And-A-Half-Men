package com.project.backend.service.impl;

import com.project.backend.DTO.DriverLocationDTO;
import com.project.backend.DTO.redis.RedisLocationsDTO;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.Vehicle;
import com.project.backend.models.VehicleType;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.repositories.VehicleRepository;
import com.project.backend.repositories.redis.DriverLocationsRepository;
import com.project.backend.service.IDriverLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverLocationService implements IDriverLocationService {

    private final DriverLocationsRepository locationsRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final RideRepository rideRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RideTracingService rideTracingService;

    @Override
    public void updateDriverLocation(Long driverId, DriverLocationDTO locationDTO) {
        locationsRepository.setLocations(
                driverId,
                locationDTO.getLongitude(),
                locationDTO.getLatitude()
        );
        rideTracingService.setRideLocation(driverId, locationDTO.getLongitude(), locationDTO.getLatitude(), locationDTO.getIsOccupied());
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();

            locationDTO.setDriverId(driverId);
            locationDTO.setDriverName(driver.firstNameAndLastName());
            locationDTO.setDriverEmail(driver.getEmail());
            locationDTO.setTimestamp(System.currentTimeMillis());
            locationDTO.setIsActive(driver.getIsActive());

            Optional<Ride> activeRideOpt = rideRepository.findFirstByDriverAndStatusIn(
                    driver, List.of(RideStatus.ACTIVE)
            );

            if (activeRideOpt.isPresent()) {
                Ride activeRide = activeRideOpt.get();
                locationDTO.setIsOccupied(true);
                locationDTO.setCurrentRideId(activeRide.getId());
            } else {
                locationDTO.setIsOccupied(false);
                locationDTO.setCurrentRideId(null);
            }

            Optional<Vehicle> vehicleOpt = vehicleRepository.findByDriverId(driver.getId());
            if (vehicleOpt.isPresent()) {
                Vehicle vehicle = vehicleOpt.get();
                if (vehicle.getVehicleType() != null) {
                    locationDTO.setVehicleTypeName(vehicle.getVehicleType().getTypeName());
                }
            }

            messagingTemplate.convertAndSend("/topic/driver-locations", locationDTO);

            System.out.println("Location updated for driver: " + driverId +
                    " at [" + locationDTO.getLatitude() + ", " +
                    locationDTO.getLongitude() + "], occupied: " + locationDTO.getIsOccupied());
        }
    }

    @Override
    public DriverLocationDTO getDriverLocation(Long driverId) {
        try {
            Point location = locationsRepository.getLocation(driverId);

            Optional<Driver> driverOpt = driverRepository.findById(driverId);
            if (driverOpt.isPresent() && location != null) {
                Driver driver = driverOpt.get();

                Optional<Ride> activeRideOpt = rideRepository.findFirstByDriverAndStatusIn(
                        driver, List.of(RideStatus.ACTIVE)
                );

                Optional<Vehicle> vehicleOpt = vehicleRepository.findByDriverId(driver.getId());

                return DriverLocationDTO.builder()
                        .driverId(driverId)
                        .driverName(driver.firstNameAndLastName())
                        .driverEmail(driver.getEmail())
                        .latitude(location.getY())
                        .longitude(location.getX())
                        .isActive(driver.getIsActive())
                        .isOccupied(activeRideOpt.isPresent())
                        .currentRideId(activeRideOpt.map(Ride::getId).orElse(null))
                        .vehicleTypeName(vehicleOpt
                                .map(Vehicle::getVehicleType)
                                .map(VehicleType::getTypeName)
                                .orElse(null))
                        .timestamp(System.currentTimeMillis())
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Error getting driver location for ID " + driverId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DriverLocationDTO> getAllDriverLocations() {
        List<RedisLocationsDTO> redisLocations = locationsRepository.getAllLocations();
        List<DriverLocationDTO> driverLocations = new ArrayList<>();

        for (RedisLocationsDTO redisLoc : redisLocations) {
            Optional<Driver> driverOpt = driverRepository.findById(redisLoc.getId());
            if (driverOpt.isPresent()) {
                Driver driver = driverOpt.get();

                Optional<Ride> activeRideOpt = rideRepository.findFirstByDriverAndStatusIn(
                        driver, List.of(RideStatus.ACTIVE)
                );

                Optional<Vehicle> vehicleOpt = vehicleRepository.findByDriverId(driver.getId());

                DriverLocationDTO locationDTO = DriverLocationDTO.builder()
                        .driverId(driver.getId())
                        .driverName(driver.firstNameAndLastName())
                        .driverEmail(driver.getEmail())
                        .latitude(redisLoc.getLatitude())
                        .longitude(redisLoc.getLongitude())
                        .isActive(driver.getIsActive())
                        .isOccupied(activeRideOpt.isPresent())
                        .currentRideId(activeRideOpt.map(Ride::getId).orElse(null))
                        .vehicleTypeName(vehicleOpt
                                .map(Vehicle::getVehicleType)
                                .map(VehicleType::getTypeName)
                                .orElse(null))
                        .timestamp(System.currentTimeMillis())
                        .build();

                driverLocations.add(locationDTO);
            }
        }

        return driverLocations;
    }

    @Override
    public List<DriverLocationDTO> getNearbyDrivers(double longitude, double latitude, double radiusKm) {
        List<RedisLocationsDTO> redisLocations =
                locationsRepository.getLocationsWithinRadius(longitude, latitude, radiusKm);

        List<DriverLocationDTO> driverLocations = new ArrayList<>();

        for (RedisLocationsDTO redisLoc : redisLocations) {
            DriverLocationDTO location = getDriverLocation(redisLoc.getId());
            if (location != null) {
                driverLocations.add(location);
            }
        }

        return driverLocations;
    }

    @Override
    public void removeDriverLocation(Long driverId) {
        locationsRepository.deactivateLocation(driverId);

        DriverLocationDTO offlineNotification = DriverLocationDTO.builder()
                .driverId(driverId)
                .latitude(null)
                .longitude(null)
                .isActive(false)
                .isOccupied(false)
                .build();

        messagingTemplate.convertAndSend("/topic/driver-locations", offlineNotification);

        System.out.println("Driver " + driverId + " location removed from Redis");
    }

    public void updateDriverOccupiedStatus(Long driverId, Long rideId, boolean isOccupied) {
        DriverLocationDTO currentLocation = getDriverLocation(driverId);

        if (currentLocation != null) {
            currentLocation.setIsOccupied(isOccupied);
            currentLocation.setCurrentRideId(rideId);

            messagingTemplate.convertAndSend("/topic/driver-locations", currentLocation);

            System.out.println("Driver " + driverId + " occupied status updated: " + isOccupied +
                    (rideId != null ? " (Ride ID: " + rideId + ")" : ""));
        } else {
            System.err.println("Cannot update occupied status - driver " + driverId +
                    " location not found in Redis");
        }
    }


    public List<DriverLocationDTO> getAvailableDriversNearby(double longitude, double latitude, double radiusKm) {
        List<DriverLocationDTO> nearbyDrivers = getNearbyDrivers(longitude, latitude, radiusKm);

        return nearbyDrivers.stream()
                .filter(driver -> driver.getIsActive() != null && driver.getIsActive())
                .filter(driver -> driver.getIsOccupied() != null && !driver.getIsOccupied())
                .toList();
    }
}