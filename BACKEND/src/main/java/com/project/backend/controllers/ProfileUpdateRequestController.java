package com.project.backend.controllers;

import com.project.backend.DTO.Profile.AdminUpdateRequestListItemDTO;
import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.service.ProfileUpdateRequestService;
import com.project.backend.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile-update-requests")
public class ProfileUpdateRequestController {

    private final ProfileUpdateRequestService service;
    private final AuthUtils authUtils;

    public ProfileUpdateRequestController(ProfileUpdateRequestService service, AuthUtils authUtils) {
        this.service = service;
        this.authUtils = authUtils;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AdminUpdateRequestListItemDTO> getRequests(
            Pageable pageable
    ) {
        return service.getRequests(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSingleRequest(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSingleRequest(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        // Implementation for approving the request goes here
        return ResponseEntity.ok().body(service.approveRequest(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        // Implementation for rejecting the request goes here
        return ResponseEntity.ok().body(service.rejectRequest(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> cancelRequest(@PathVariable Long id) {
        AppUser user = authUtils.getCurrentUser();
        return ResponseEntity.ok(service.cancelRequest((Driver) user, id));
    }
}
