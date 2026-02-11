package com.project.backend.controllers.ride;

import com.project.backend.DTO.Ride.CostTimeDTO;
import com.project.backend.DTO.Ride.RideBookingParametersDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.filters.RideFilter;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.service.IHistoryService;
import com.project.backend.service.IRideService;
import com.project.backend.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideQueryController {
    private final IHistoryService historyService;
    private final IRideService rideService;
    private final AuthUtils authUtils;

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDTO> getRide(
            @PathVariable Long id
    ) {
        RideResponseDTO ride = rideService.getRideById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ride);
    }

    @GetMapping("/history")
    public ResponseEntity<PagedResponse<RideResponseDTO>> getRideHistory(
            @ModelAttribute RideFilter filter
    ) {
        AppUser currentUser = authUtils.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PagedResponse<RideResponseDTO> history = historyService.getRideHistory(filter, currentUser);
        return ResponseEntity.ok(history);
    }

    // TODO
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RideResponseDTO>> getActiveRides(
            @RequestParam(required = false) String name
    ) {
        List<RideResponseDTO> result = rideService.getActiveRides(name);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // TODO
    @GetMapping("/booked")
    public ResponseEntity<?> getBookedRides() {
        Customer user = authUtils.getCurrentCustomer();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<RideResponseDTO> bookedRides = rideService.getAllBookedRidesByCustomer(user);
        return ResponseEntity.ok(bookedRides);
    }

    @PostMapping("/estimates")
    public ResponseEntity<?> estimateRide(
            @RequestBody RideBookingParametersDTO rideData
    ) {
        try {
            CostTimeDTO estimate = rideService.estimateRide(rideData);
            return ResponseEntity.status(HttpStatus.OK).body(estimate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
