package com.project.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    @PostMapping("/estimates")
    public ResponseEntity<?> estimateRide(@RequestBody Map<String, Object> rideData) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
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

    @PatchMapping("/{id}/finish")
    public ResponseEntity<?> finishRide(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable String id) {
        return ResponseEntity.status(501)
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
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
}