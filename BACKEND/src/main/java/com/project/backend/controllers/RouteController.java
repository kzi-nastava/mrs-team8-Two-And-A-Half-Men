package com.project.backend.controllers;

import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.service.RouteService;
import com.project.backend.util.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteService routeService;
    private final AuthUtils authUtils;

    public RouteController(RouteService routeService, AuthUtils authUtils) {
        this.routeService = routeService;
        this.authUtils = authUtils;
    }

    @GetMapping("/favourites")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getFavoriteRoutes() {
        var customer = authUtils.getCurrentCustomer();
        if (customer == null) {
            throw new ForbiddenException("User is not authenticated as a customer");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.getAllFavourites(customer.getId()));
    }

    @PostMapping("/{id}/favourites")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> addFavoriteRoute(@PathVariable Long id) {

        var customer = authUtils.getCurrentCustomer();
        if (customer == null) {
            throw new ForbiddenException("User is not authenticated as a customer");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.addToFavourites(id, customer.getId()));
    }

    @DeleteMapping("/{id}/favourites")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> removeFavoriteRoute(@PathVariable Long id) {
        var customer = authUtils.getCurrentCustomer();
        if (customer == null) {
            throw new ForbiddenException("User is not authenticated as a customer");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routeService.removeFromFavourites(id, customer.getId()));
    }
}
