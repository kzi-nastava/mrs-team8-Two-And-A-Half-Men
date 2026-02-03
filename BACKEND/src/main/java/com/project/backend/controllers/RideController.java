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
import com.project.backend.models.actor.AccessTokenPassengerActor;
import com.project.backend.models.actor.JwtPassengerActor;
import com.project.backend.models.actor.PassengerActor;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @PostMapping("/{id}/notes")
    public ResponseEntity<NoteResponseDTO> addRideNote(
            @PathVariable Long id,
            @RequestParam(name = "accessToken", required = false) String accessToken,
            @RequestBody NoteRequestDTO noteRequest
    ) {
        PassengerActor actor = getPassengerActor(accessToken);

        NoteResponseDTO note = rideService.saveRideNote(id, actor, noteRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(note);
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
    public ResponseEntity<?> finishRide(
            @PathVariable Long id,
            @RequestBody FinishRideDTO finishRideDTO
    ) {
        rideService.finishRide(id, finishRideDTO);
        return ResponseEntity.status(HttpStatus.OK).body(null);
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
        PassengerActor actor = getPassengerActor(accessToken);

        RatingResponseDTO response = ratingService.rateRide(id, actor, ratingRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<PagedResponse<RideResponseDTO>> getDriverRideHistory(
            Pageable pageable,
            @RequestParam(name = "startDate", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        AppUser user = authUtils.getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        PagedResponse<RideResponseDTO> history = null;

        if (user instanceof Driver) {
            history = historyService.getDriverRideHistory(user.getId(), pageable, startDate, endDate);
        }
        if( user instanceof Customer ) {
            history = historyService.getCustomerRideHistory((Customer) user, pageable, startDate, endDate);
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

    @GetMapping("/active")
    public ResponseEntity<RideTrackingDTO> getActiveRide(
            @RequestParam(name = "accessToken", required = false) String accessToken
    ) {
        RideTrackingDTO ride;

        Driver driver = authUtils.getCurrentDriver();
        if (driver == null) {
            PassengerActor actor = getPassengerActor(accessToken);
            ride = rideService.getRideTrackingInfo(actor);
        } else {
            ride = rideService.getDriversActiveRide(driver);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ride);
    }

    private PassengerActor getPassengerActor(String accessToken) {
        PassengerActor actor;

        AppUser user = authUtils.getCurrentUser();

        if (user != null) {
            actor = new JwtPassengerActor((Customer) user);
        } else if (accessToken != null) {
            actor = new AccessTokenPassengerActor(accessToken);
        } else {
            throw new UnauthorizedException("Authentication required");
        }

        return actor;
    }
    @GetMapping("/booked")
    public ResponseEntity<List<RideBookedDTO>> getBookedRides() {
        Customer user = authUtils.getCurrentCustomer();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<RideBookedDTO> bookedRides = rideService.getAllBookedRidesByCustomer(user);
        return ResponseEntity.ok(bookedRides);
    }
}