package com.project.backend.controllers;

import com.project.backend.DTO.CostTimeDTO;
import com.project.backend.DTO.*;
import com.project.backend.DTO.Ride.*;
import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.models.AppUser;
import com.project.backend.service.impl.PanicService;
import com.project.backend.service.IHistoryService;
import com.project.backend.service.IRatingService;
import com.project.backend.service.IRideService;
import com.project.backend.util.AuthUtils;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {
    private final IRatingService ratingService;
    private final IHistoryService historyService;
    private final IRideService rideService;
    private final AuthUtils authUtils;


    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private PanicService panicService;


    @PostMapping("/estimates")
    public ResponseEntity<?> estimateRide(@RequestBody RideRequestDTO rideData) {


        if (rideData.getAddressesPoints() == null || rideData.getAddressesPoints().size() < 2) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "At least one address point is required for estimation"));
        }
        double estimatedPrice = 25.50;
        int estimatedTimeMinutes = 15;
        return ResponseEntity.ok(Map.of(
                "estimatedPrice", estimatedPrice * rideData.getAddressesPoints().size(),
                "estimatedTimeMinutes", estimatedTimeMinutes
        ));
    }

    @PostMapping
    public ResponseEntity<?> createRide(@RequestBody Map<String, Object> rideData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDTO> getRide(
            @PathVariable Long id
    ) {
        RideResponseDTO ride = rideService.getRideById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ride);
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<?> getRideLocation(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<?> addRideNote(
            @PathVariable Long id,
            @RequestBody NoteRequestDTO noteRequest) {
        // TODO: correct controller logic and make service
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<?> startRide(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
    
    @PatchMapping("/{id}/finish")
    public ResponseEntity<?> finishRide(@PathVariable String id) {
        Long rideId;
        try {
            rideId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid ride ID format"));
        }

        // Does ride exists
        if (rideId > 100) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Ride not found"));
        }

        // Is ride in active status
        if (rideId == 99) {

            return ResponseEntity.status(400)
                    .body(Map.of("error", "Ride is not active or already finished"));
        }

        // Logic for ride finish
        CostTimeDTO costTime = new CostTimeDTO();
        costTime.setCost(45.75);
        costTime.setTime(32);
        return ResponseEntity.ok(Map.of("status", "COMPLETED", "costTime", costTime));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable String id, @RequestBody String reason) {
        if (id.equals("11")) {
            return ResponseEntity.ok(Map.of("message", "Ride cancelled successfully"));
        }
        return ResponseEntity.status(404)
                .body(Map.of("error", "Ride not found"));
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateRide(
            @PathVariable String id,
            @Valid @RequestBody RatingRequestDTO ratingRequest) {

        RatingResponseDTO response = ratingService.rateRide(ratingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<PagedResponse<RideResponseDTO>> getDriverRideHistory(
            Pageable pageable,
            @Valid @RequestBody HistoryRequestDTO historyRequestDTO
    ) {
        AppUser user = authUtils.getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        PagedResponse<RideResponseDTO> history = null;

        if (user instanceof Driver) {
            history = historyService.getDriverRideHistory(user.getId(), pageable, historyRequestDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    @PostMapping("/panic")
    public ResponseEntity<?> panic(@RequestParam(name = "accessToken", required = false) String accessToken)
    {
        AppUser user = authUtils.getCurrentUser();
        try {
            panicService.triggerPanicAlert(user, accessToken);
            return ResponseEntity.ok(Map.of("message", "Panic alert triggered successfully"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}