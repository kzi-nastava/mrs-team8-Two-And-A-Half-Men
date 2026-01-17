package com.project.backend.controllers;

import com.project.backend.DTO.CostTimeDTO;
import com.project.backend.DTO.*;
import com.project.backend.DTO.Ride.RideCancelationDTO;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.models.AppUser;
import com.project.backend.models.enums.UserRole;
import com.project.backend.service.impl.CancellationService;
import com.project.backend.service.impl.RatingService;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {
    private final RatingService ratingService;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private CancellationService cancellationService;
    @PostMapping("/estimates")
    public ResponseEntity<?> estimateRide(@RequestBody RidesInfoRequestDTO rideData) {



        if(rideData.getAddressesPoints() == null || rideData.getAddressesPoints().size() < 2) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "At least one address point is required for estimation") );
        }
        double estimatedPrice = 25.50;
        int estimatedTimeMinutes = 15;
        return ResponseEntity.ok(Map.of(
                "estimatedPrice", estimatedPrice * rideData.getAddressesPoints().size() ,
                "estimatedTimeMinutes", estimatedTimeMinutes
        ));
    }

    @PostMapping
    public ResponseEntity<?> createRide(@RequestBody Map<String, Object> rideData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRide(@PathVariable String id) {
        Long rideId;
        try {
            rideId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid ride ID format"));
        }

        // Check if ride exists
        if (rideId > 100) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Ride not found"));
        }

        // Create dummy ride data
        RidesInfoDTO ride = createDummyRideInfo(rideId);

        return ResponseEntity.ok(ride);
    }

    private RidesInfoDTO createDummyRideInfo(Long rideId) {
        ArrayList<String> services = new ArrayList<>();
        services.add("pet-friendly");
        services.add("child-seat");
        services.add("wifi");

        ArrayList<String> addresses = new ArrayList<>();
        addresses.add("Trg Republike, Beograd");
        addresses.add("Knez Mihailova 45, Beograd");
        addresses.add("Aerodrom Nikola Tesla, Beograd");

        return new RidesInfoDTO(
                "SEDAN",
                services,
                addresses,
                LocalDateTime.now().plusHours(1)
        );
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<?> getRideLocation(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<?> addRideNote(
            @PathVariable String id,
            @RequestBody NoteRequestDTO noteRequest) {
        Long rideId;
        try {
            rideId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid ride ID format"));
        }

        // Dummy validation if ride exists
        if (rideId > 100) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Ride not found"));
        }

        // Text validation
        if (noteRequest.getNoteText() == null || noteRequest.getNoteText().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Note text is required"));
        }

        // Check if ride is active
        if (rideId == 99) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Cannot add note to completed or cancelled ride"));
        }

        // Dummy send to service
        NoteDTO createdNote = new NoteDTO(
                noteRequest.getNoteText()
        );

        return ResponseEntity.status(201).body(createdNote);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<?> startRide(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
    /*
    Here we save data of ride in our system evry 30 seconds we send locations to server(use it for updating ride and at finish we just send id of ride)
    (futer me need it)
    it returs calculated price for ride
     */
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
        return ResponseEntity.ok(Map.of("status", "COMPLETED" , "costTime", costTime));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable Long id, @RequestBody RideCancelationDTO reason) {
        try {
            AppUser user = authUtils.getCurrentUser();
            if(user == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Unauthorized"));
            }
            cancellationService.cancelRide(id, reason, user);
        } catch (UnauthorizedException e) {

            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Internal server error"));
        }
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateRide(
            @PathVariable String id,
            @Valid @RequestBody RatingRequestDTO ratingRequest) {

        RatingResponseDTO response = ratingService.rateRide(ratingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getRideHistory(@RequestParam(required = false) Map<String, String> filters) {
        ArrayList<HistoryDTO> history = new ArrayList<>();
        history.add( new HistoryDTO() );
        history.add(new HistoryDTO());
        return ResponseEntity.ok(Map.of("history", history));
    }
}