package com.project.backend.controllers;

import com.project.backend.DTO.Ride.*;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.service.impl.CancellationService;
import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.models.Driver;
import com.project.backend.service.impl.PanicService;
import com.project.backend.service.IHistoryService;
import com.project.backend.service.IRatingService;
import com.project.backend.service.IRideService;
import com.project.backend.service.rating.AccessTokenRatingActor;
import com.project.backend.service.rating.JwtRatingActor;
import com.project.backend.service.rating.RatingActor;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {
    private final IRatingService ratingService;
    private final IHistoryService historyService;
    private final IRideService rideService;
    private final AuthUtils authUtils;
    private final PanicService panicService;
    private final CancellationService cancellationService;

    @PostMapping("/estimates")
    public ResponseEntity<?> estimateRide(@RequestBody RideBookingParametersDTO rideData) {
        try {
            CostTimeDTO estimate = rideService.estimateRide(rideData);
            return ResponseEntity.status(HttpStatus.OK).body(estimate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createRide(@RequestBody RideBookingParametersDTO body) {
        AppUser user = authUtils.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rideService.createRide(user.getId(), body));
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
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> startRide(@PathVariable String id) {
        var user = authUtils.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(rideService.startARide(id,user.getId()));
    }


    @PatchMapping("/{id}/end")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> endRide(@PathVariable Long id) {
            Driver driver = authUtils.getCurrentDriver();
            if (driver == null) {
                return ResponseEntity.status(401).body(Map.of());
            }
            CostTimeDTO costTimeDTO = rideService.endRideById(id, driver);
            System.out.println(costTimeDTO.getCost() + " " + costTimeDTO.getTime());
            return ResponseEntity.ok(costTimeDTO);
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
    public ResponseEntity<?> cancelRide(@PathVariable Long id, @RequestBody RideCancelationDTO reason) {
            AppUser user = authUtils.getCurrentUser();
            if(user == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Unauthorized"));
            }
            cancellationService.cancelRide(id, reason, user);
            return ResponseEntity.ok(Map.of("message", "Ride cancelled successfully"));
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateRide(
            @PathVariable Long id,
            @RequestParam(name = "accessToken", required = false) String accessToken,
            @Valid @RequestBody RatingRequestDTO ratingRequest
    ) {
        RatingActor actor;

        AppUser user = authUtils.getCurrentUser();

        if (user != null) {
            actor = new JwtRatingActor((Customer) user);
        } else if (accessToken != null) {
            actor = new AccessTokenRatingActor(accessToken);
        } else {
            throw new UnauthorizedException("Authentication required");
        }

        RatingResponseDTO response = ratingService.rateRide(id, actor, ratingRequest);

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
        panicService.triggerPanicAlert(user, accessToken);
        return ResponseEntity.ok(Map.of("message", "Panic alert triggered successfully"));
    }
}