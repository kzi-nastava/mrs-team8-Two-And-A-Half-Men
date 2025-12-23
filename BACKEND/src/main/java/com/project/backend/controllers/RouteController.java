package com.project.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoriteRoutes() {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @PostMapping("/{id}/favorites")
    public ResponseEntity<?> addFavoriteRoute(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }

    @DeleteMapping("/{id}/favorites")
    public ResponseEntity<?> removeFavoriteRoute(@PathVariable String id) {
        return ResponseEntity.status(501)
                .body(Map.of("error", "Not implemented"));
    }
}
