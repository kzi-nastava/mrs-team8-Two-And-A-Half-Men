package com.project.backend.controllers;

import com.project.backend.DTO.DriverLocationDTO;
import com.project.backend.service.IDriverLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DriverController {

    private final IDriverLocationService driverLocationService;

    /**
     * Get all driver locations
     * GET /api/v1/drivers/locations
     */
    @GetMapping("/locations")
    public ResponseEntity<List<DriverLocationDTO>> getAllDriverLocations() {
        try {
            DriverLocationDTO driverLocation = new DriverLocationDTO(
                    101L,
                    "abc",
                    "abc@gmail.com",
                    45.740052D,
                    19.838116D,
                    false,
                    true,
                    null,
                    "car",
                    123L
            );
            driverLocationService.updateDriverLocation(101L, driverLocation);

            List<DriverLocationDTO> locations = driverLocationService.getAllDriverLocations();
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            System.err.println("Error fetching all driver locations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get specific driver location by ID
     * GET /api/v1/drivers/locations/{driverId}
     */
    @GetMapping("/locations/{driverId}")
    public ResponseEntity<DriverLocationDTO> getDriverLocation(@PathVariable Long driverId) {
        try {
            DriverLocationDTO driverLocation = new DriverLocationDTO(
                    101L,
                    "abc",
                    "abc@gmail.com",
                    45.240052D,
                    19.838116D,
                    false,
                    true,
                    null,
                    "car",
                    123L
            );
            driverLocationService.updateDriverLocation(101L, driverLocation);

            DriverLocationDTO location = driverLocationService.getDriverLocation(driverId);
            if (location != null) {
                return ResponseEntity.ok(location);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching driver location: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get nearby drivers within a radius
     * GET /api/v1/drivers/locations/nearby?longitude=20.4569&latitude=44.8176&radiusKm=5
     */
    @GetMapping("/locations/nearby")
    public ResponseEntity<List<DriverLocationDTO>> getNearbyDrivers(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "5") double radiusKm) {
        try {
            List<DriverLocationDTO> nearbyDrivers =
                    driverLocationService.getAvailableDriversNearby(longitude, latitude, radiusKm);
            return ResponseEntity.ok(nearbyDrivers);
        } catch (Exception e) {
            System.err.println("Error fetching nearby drivers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get available (not occupied) drivers nearby
     * GET /api/v1/drivers/locations/nearby/available?longitude=20.4569&latitude=44.8176&radiusKm=5
     */
    @GetMapping("/locations/nearby/available")
    public ResponseEntity<List<DriverLocationDTO>> getAvailableDriversNearby(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "5") double radiusKm) {
        try {
            List<DriverLocationDTO> availableDrivers =
                    driverLocationService.getNearbyDrivers(longitude, latitude, radiusKm);
            return ResponseEntity.ok(availableDrivers);
        } catch (Exception e) {
            System.err.println("Error fetching available drivers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update driver location (typically called by driver app)
     * PUT /api/v1/drivers/locations/{driverId}
     */
    @PutMapping("/locations/{driverId}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateDriverLocation(
            @PathVariable Long driverId,
            @RequestBody DriverLocationDTO locationDTO) {
        try {
            driverLocationService.updateDriverLocation(driverId, locationDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error updating driver location: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove driver location (when driver goes offline)
     * DELETE /api/v1/drivers/locations/{driverId}
     */
    @DeleteMapping("/locations/{driverId}")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeDriverLocation(@PathVariable Long driverId) {
        try {
            driverLocationService.removeDriverLocation(driverId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error removing driver location: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * WebSocket endpoint - Driver sends location updates via WebSocket
     * Destination: /app/driver/location
     * Broadcasts to: /topic/driver-locations
     */
    @MessageMapping("/driver/location")
    @SendTo("/topic/driver-locations")
    public DriverLocationDTO handleDriverLocationUpdate(DriverLocationDTO locationDTO) {
        System.out.println("WebSocket: Received location update for driver: " + locationDTO.getDriverId());

        if (locationDTO.getDriverId() != null) {
            driverLocationService.updateDriverLocation(
                    locationDTO.getDriverId(),
                    locationDTO
            );
        }

        return locationDTO;
    }
}