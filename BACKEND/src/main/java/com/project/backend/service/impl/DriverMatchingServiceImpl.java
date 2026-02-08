package com.project.backend.service.impl;

import com.project.backend.DTO.internal.ride.FindDriverDTO;
import com.project.backend.DTO.internal.ride.FindDriverFilter;
import com.project.backend.DTO.redis.RedisLocationsDTO;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.models.AdditionalService;
import com.project.backend.models.Ride;
import com.project.backend.models.Vehicle;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.repositories.VehicleRepository;
import com.project.backend.repositories.redis.DriverLocationsRepository;
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

    @Override
    public Optional<FindDriverDTO> findDriverFor(Ride ride) {
        var startCoordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash()).get(0);
        var filtersBuilder = FindDriverFilter.builder()
                .longitude(startCoordinates.getLongitude())
                .latitude(startCoordinates.getLatitude());

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

            // There are some suitable driver, find the closest one
            var closestInfo = getClosestDriver(filter, drivers);
            var driver = driverRepository.findById(closestInfo.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Driver with id " +closestInfo.getId() + " does not exist")
            );
            return Optional.of(new FindDriverDTO(driver, closestInfo.getTotalDistance()));
        }
        // No suitable driver found
        return Optional.empty();
    }





    private List<RedisLocationsDTO> getRedisLocationsDTOS(double latitude, double longitude, Integer KM) {
        if (KM > 0) {
            return driverLocationsRepository
                    .getLocationsWithinRadius(
                            longitude,
                            latitude,
                            KM
                    );
        }
        else {
            return driverLocationsRepository.getAllLocations();
        }
    }

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

    private void getRideData(Set<Long> driversIds, Map<Long, DriverInformation> drivers) {
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

    private boolean doesVehicleMatch(Vehicle vehicle, FindDriverFilter filter) {
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

    private static void mapLocationsToDriversInformation(
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
        double rideEndDistance = 0;
        double toStartPointDistance = 0;
        public double getTotalDistance() {
            return rideEndDistance + toStartPointDistance;
        }
    }
}
