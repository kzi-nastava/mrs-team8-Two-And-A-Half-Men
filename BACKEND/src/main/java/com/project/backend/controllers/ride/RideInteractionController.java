package com.project.backend.controllers.ride;

import com.project.backend.DTO.Ride.*;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.actor.AccessTokenPassengerActor;
import com.project.backend.models.actor.JwtPassengerActor;
import com.project.backend.models.actor.PassengerActor;
import com.project.backend.service.IRatingService;
import com.project.backend.service.IRideService;
import com.project.backend.service.RideBookingService;
import com.project.backend.service.impl.PanicService;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideInteractionController {
    private final IRatingService ratingService;
    private final IRideService rideService;
    private final AuthUtils authUtils;
    private final PanicService panicService;
    private final RideBookingService rideBookingService;

    @PostMapping
    public ResponseEntity<?> createRide(
            @RequestBody RideBookingParametersDTO body
    ) {
        AppUser user = authUtils.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rideBookingService.bookRide(user.getId(), body));
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

    @PostMapping("/panic")
    public ResponseEntity<?> panic(
            @RequestParam(name = "accessToken", required = false) String accessToken
    ) {
        AppUser user = authUtils.getCurrentUser();
        panicService.triggerPanicAlert(user, accessToken);
        return ResponseEntity.ok(Map.of("message", "Panic alert triggered successfully"));
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
}
