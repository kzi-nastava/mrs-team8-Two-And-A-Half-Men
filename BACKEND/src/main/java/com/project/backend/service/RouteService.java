package com.project.backend.service;

import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Customer;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.RouteRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RouteService {

    private final AppUserRepository appUserRepository;
    private final RouteRepository routeRepository;

    public RouteService(AppUserRepository appUserRepository, RouteRepository routeRepository) {
        this.appUserRepository = appUserRepository;
        this.routeRepository = routeRepository;
    }


    public Object getAllFavourites(Long id) {
        var customer = getCustomer(id);
        return customer.getFavoriteRoutes();
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
