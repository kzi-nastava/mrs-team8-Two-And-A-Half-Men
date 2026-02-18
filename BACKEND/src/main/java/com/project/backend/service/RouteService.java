package com.project.backend.service;

import com.project.backend.DTO.Route.RouteItemDTO;
import com.project.backend.DTO.Route.FavouriteRouteDTO;
import com.project.backend.DTO.Route.FavouriteRouteItemDTO;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.models.Customer;
import com.project.backend.models.Location;
import com.project.backend.models.Route;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.LocationRepository;
import com.project.backend.repositories.RouteRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {

    private final AppUserRepository appUserRepository;
    private final RouteRepository routeRepository;
    private final LocationTransformer locationTransformer;
    private final LocationRepository locationRepository;

    public RouteService(
            AppUserRepository appUserRepository,
            RouteRepository routeRepository,
            LocationTransformer locationTransformer,
            LocationRepository locationRepository) {
        this.appUserRepository = appUserRepository;
        this.routeRepository = routeRepository;
        this.locationTransformer = locationTransformer;
        this.locationRepository = locationRepository;
    }

    /**
     * Creates a new route from the stop points provided. Each point that does not already exist in the database
     * is added to locations for easier fetching of addresses
     * @param points list of points that make up the route
     * @return newly created Route
     */
    public Route createNew(List<RouteItemDTO> points) {

        if(points == null || points.isEmpty()) {
            throw new BadRequestException("At least 2 points are need to create a route");
        }

        List<double[]> coordinates = new ArrayList<>();
        Set<Location> locations = new HashSet<>();

        for (var point : points) {
            // Collect coordinates for geohash transformation
            double[] coordinate = {point.getLatitude(), point.getLongitude()};
            coordinates.add(coordinate);
            // Handle location creation
            locations.add(
                    Location.builder()
                            .address(point.getAddress())
                            .latitude(point.getLatitude())
                            .longitude(point.getLongitude())
                            .geoHash(locationTransformer.transformFromPoints(List.of(coordinate)))
                            .build()
            );
        }

        // Create route geohash
        String routeGeohash = locationTransformer.transformFromPoints(coordinates);
        Route route = new Route(null, routeGeohash);

        // Remove existing locations
        List<Location> existingLocations = locationRepository.findAllByGeoHashIn(locations.stream().map(Location::getGeoHash).toList());
        List<String> existingGeohashes = existingLocations.stream().map(Location::getGeoHash).toList();
        locations.removeIf(loc -> existingGeohashes.contains(loc.getGeoHash()));

        // Save route and locations
        routeRepository.save(route);
        locationRepository.saveAll(locations);

        return route;
    }

    public Map<String, Object> getAllFavourites(Long id) {
        var customer = getCustomer(id);
        var routes = new ArrayList<FavouriteRouteDTO>();
        // Map all routes to DTOs
        for (Route route : customer.getFavoriteRoutes()) {
            // Get coordinate list from route's geohash
            List<Coordinates> coordinates = locationTransformer.transformToCoordinates(route.getGeoHash());

            // Get hashes of each individual coordinate
            List<String> hashes = coordinates
                    .stream().map(
                            c -> locationTransformer
                                    .transformFromPoints(List.of(new double[] {c.getLatitude(), c.getLongitude()}))
                    ).toList();
            // Get locations with the same hash - we need address
            var locations = locationRepository.findAllByGeoHashIn(hashes);
            // Map every point to DTO and add it to the list
            var coordinatesDTOs = new ArrayList<FavouriteRouteItemDTO>();
            for (int i = 0; i < (long) coordinates.size(); ++i) {
                Coordinates coordinate = coordinates.get(i);
                String hash = hashes.get(i);
                coordinatesDTOs.add(
                        FavouriteRouteItemDTO.builder()
                                .latitude(coordinate.getLatitude())
                                .longitude(coordinate.getLongitude())
                                // Find address of the first location with the same geohash or null
                                // - no address in the db, front need to fetch it
                                .address(
                                        locations.stream().filter(
                                        location -> location.getGeoHash().equals(hash)
                                    ).map(Location::getAddress).findFirst().orElse(null)
                                )
                                .build()
                );
            }

            routes.add(new FavouriteRouteDTO(route.getId(), coordinatesDTOs));
        }

        return Map.of("routes", routes);
    }

    public Map<String, Object> addToFavourites(Long routeId, Long customerId) {
        var customer = getCustomer(customerId);
        var route = routeRepository
                .findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        customer.getFavoriteRoutes().add(route);
        appUserRepository.save(customer);
        return Map.of(
                "ok", true,
                "routeId", routeId,
                "customerId", customerId,
                "status", "added to favourites"
        );
    }

    public Map<String, Object> removeFromFavourites(Long routeId, Long customerId) {
        var customer = getCustomer(customerId);
        customer.getFavoriteRoutes().removeIf(r -> r.getId().equals(routeId));
        appUserRepository.save(customer);
        return Map.of(
                "ok", true,
                "routeId", routeId,
                "customerId", customerId,
                "status", "removed from favourites"
        );
    }

    private @NonNull Customer getCustomer(Long customerId) {
        var user = appUserRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (user.getRole() != UserRole.CUSTOMER) {
            throw new ForbiddenException("User is not a customer");
        }
        return (Customer) user;
    }
}
