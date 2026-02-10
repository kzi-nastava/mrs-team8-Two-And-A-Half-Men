package com.project.backend.controllers;

import com.project.backend.service.NotificationsService;
import com.project.backend.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationsController {
    private final AuthUtils authUtils;
    private final NotificationsService notificationsService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        var user = authUtils.getCurrentUser();
        return ResponseEntity.ok(notificationsService.getAll(user.getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        var user = authUtils.getCurrentUser();
        return ResponseEntity.ok(notificationsService.markRead(user.getId(), id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        var user = authUtils.getCurrentUser();
        return ResponseEntity.ok(notificationsService.markAllRead(user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        var user = authUtils.getCurrentUser();
        return ResponseEntity.ok(notificationsService.deleteById(user.getId(), id));
    }

    @DeleteMapping("/read")
    public ResponseEntity<?> deleteRead() {
        var user = authUtils.getCurrentUser();
        return ResponseEntity.ok(notificationsService.deleteRead(user.getId()));
    }
}
