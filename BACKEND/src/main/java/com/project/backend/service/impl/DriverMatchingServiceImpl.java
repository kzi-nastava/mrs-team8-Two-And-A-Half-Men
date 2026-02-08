package com.project.backend.service.impl;

import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.DTO.redis.RedisLocationsDTO;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.models.AdditionalService;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.Vehicle;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.repositories.VehicleRepository;
import com.project.backend.repositories.redis.DriverLocationsRepository;
import com.project.backend.service.DateTimeService;
import com.project.backend.service.DriverMatchingService;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DriverMatchingServiceImpl implements DriverMatchingService {

    private final List<Integer> KM_RANGE = List.of(30, 50, 70, 100, -1);

    private final DriverLocationsRepository driverLocationsRepository;
    private final RideRepository rideRepository;
    private final VehicleRepository vehicleRepository;
    private final LocationTransformer locationTransformer;
    private final CoordinatesFactory coordinatesFactory;
    private final DriverRepository driverRepository;
    private final DateTimeService dateTimeService;

    @Override
    public Optional<FindDriverDTO> findDriverFor(Ride ride) {
        var startCoordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash()).get(0);
        var filtersBuilder = FindDriverFilter.builder()
                .longitude(startCoordinates.getLongitude())
                .latitude(startCoordinates.getLatitude())
                .numberOfPassengers(ride.getPassengers().size());

        if (ride.getAdditionalServices() != null) {
            filtersBuilder.additionalServicesIds(
                    ride.getAdditionalServices()
                            .stream()
                            .map(AdditionalService::getId)
                            .toList()
            );
        }

        if (ride.getVehicleType() != null) {
            filtersBuilder.vehicleTypeId(ride.getVehicleType().getId());
        }

        return findBestDriver(filtersBuilder.build());
    }

    @Override
    public Optional<FindDriverDTO> findBestDriver(FindDriverFilter filter) {
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

            checkDriversActivity(driversIds, drivers);
            // No more driver's continue further
            if (drivers.isEmpty()) {
                continue;
            }

            // There are some suitable driver, find the closest one
            var closestInfo = getClosestDriver(filter, drivers);
            return Optional.of(new FindDriverDTO(closestInfo.getDriver(), closestInfo.getTotalDistance()));
        }
        // No suitable driver found
        return Optional.empty();
    }

    /**
     * Fetch all active drivers from redis in a circle of radius specified by parameter with center at a specific point.
     * @param latitude latitude of the center point
     * @param longitude longitude of the center point
     * @param radius radius in witch to search
     * @return List of driver locations from Redis
     */
    private List<RedisLocationsDTO> getRedisLocationsDTOS(double latitude, double longitude, Integer radius) {
        if (radius > 0) {
            return driverLocationsRepository
                    .getLocationsWithinRadius(
                            longitude,
                            latitude,
                            radius
                    );
        }
        else {
            return driverLocationsRepository.getAllLocations();
        }
    }

    /**
     * Find the closest driver to the start point of the ride. If driver has an active ride,
     * the distance to the end point of that ride is also included in the calculation.
     * @param filter object containing the start point coordinates
     * @param drivers map of drivers to check, with their coordinates and active ride (if they have one)
     * @return DriverInformation object of the closest driver, containing also the distance to the start point
     * and end point of active ride (if they have one)
     */
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

    /**
     * Fetch ride information about each possible driver.
     * @param driversIds list of possible drivers id's to fetch rides for
     * @param drivers map that holds driver's information, to which the ride information will be added
     */
    private void getRideData(Set<Long> driversIds, Map<Long, DriverInformation> drivers) {
        // TODO: add appropriate handling with scheduled rides
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

    /**
     * Fetches vehicle information about every candidate driver and checks if those vehicles satisfy the filters.
     * Drivers whose vehicles do not match the filter are excluded from the list of possible suitable drivers
     * @param filter object containing all the filters
     * @param driversIds id's of currently possible drivers
     * @param drivers map that holds driver's information
     */
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

    /**
     * Check if vehicle matches filter
     * @param vehicle vehicle to check
     * @param filter values a vehicle must possess for driver to be suitable
     * @return true if the vehicle matches the filter, false otherwise
     */
    private boolean doesVehicleMatch(Vehicle vehicle, FindDriverFilter filter) {
        // Check if number of seats is sufficient
        if (vehicle.getNumberOfSeats() < filter.getNumberOfPassengers()) {
            return false;
        }
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

    /**
     * Transform driver locations from redis into private data structure for the algorithm of searching
     * excluding any driver previously seen in the algorithm
     * @param driversLocations redis locations to map
     * @param checkedIds set of ids of all drivers that have already been processed
     * @param driversIds set of currently viable drivers (possible)
     * @param drivers map which stores driver's data under the driver's id as key
     */
    private void mapLocationsToDriversInformation(
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

    /**
     * Check drivers activity and exclude those that have more than 8 hours of activity
     * in the last 24 hours
     * @param driversIds id's of currently possible drivers
     * @param drivers map that holds driver's information, to which the activity information will be added and checked
     */
    private void checkDriversActivity(Set<Long> driversIds, Map<Long, DriverInformation> drivers) {
        var dbDrivers = driverRepository.findAllById(driversIds);
        for (var driver : dbDrivers) {
            var driverInfo = drivers.get(driver.getId());
            driverInfo.setDriver(driver);
            var now = dateTimeService.getCurrentDateTime();
            var dayAgo = now.minusHours(24);
            var activity = driver.calculateActivityRange(dayAgo, now, now);
            if (activity >= 8 * 60) {
                drivers.remove(driver.getId());
                driversIds.remove(driver.getId());
            }
        }
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
        Driver driver;
        double rideEndDistance = 0;
        double toStartPointDistance = 0;
        public double getTotalDistance() {
            return rideEndDistance + toStartPointDistance;
        }
    }
}
