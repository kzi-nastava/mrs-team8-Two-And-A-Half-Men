package com.project.backend.controllers;

import com.project.backend.DTO.HistoryDTO;
import com.project.backend.DTO.RidesInfoRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    @PostMapping("/estimates")
    public ResponseEntity<?> estimateRide(@RequestBody RidesInfoRequestDTO rideData) {

        // Dummy estimation logic

        if(rideData.getAddreessPoints() == null || rideData.getAddreessPoints().size() < 2) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "At least one address point is required for estimation") );
        }
        double estimatedPrice = 25.50;
        int estimatedTimeMinutes = 15;
        return ResponseEntity.ok(Map.of(
                "estimatedPrice", estimatedPrice * rideData.getAddreessPoints().size() ,
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
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<?> getRideLocation(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<?> addRideNote(@PathVariable String id,
                                         @RequestBody Map<String, Object> noteData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
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
        return ResponseEntity.status(200)
                .body(Map.of("message", "Ride finished successfully" ,"totalPrice",123.45));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable String id, @RequestBody String reason) {
        if(id.equals("11")){
            return ResponseEntity.ok(Map.of("message", "Ride cancelled successfully"));
        }
        return  ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateRide(@PathVariable String id,
                                      @RequestBody Map<String, Object> ratingData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getRideHistory(@RequestParam(required = false) Map<String, String> filters) {
        ArrayList<HistoryDTO> history = new ArrayList<>();
        history.add( new HistoryDTO() );
        history.add(new HistoryDTO());
        return ResponseEntity.ok(Map.of("history", history));
    }
}