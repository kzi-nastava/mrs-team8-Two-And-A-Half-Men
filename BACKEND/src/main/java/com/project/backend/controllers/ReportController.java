package com.project.backend.controllers;

import com.project.backend.DTO.reports.AggregatedReportDTO;
import com.project.backend.DTO.reports.RideReportDTO;
import com.project.backend.DTO.reports.RideReportRequest;
import com.project.backend.service.ReportService;
import com.project.backend.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports/rides")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final AuthUtils authUtils;

    /**
     * Get ride report for current authenticated user (driver or passenger)
     * GET /api/reports/rides/my-report?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/my-report")
    @PreAuthorize("hasAnyRole('DRIVER', 'CUSTOMER')")
    public ResponseEntity<RideReportDTO> getMyRideReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        // Get current user ID from authentication
        Long userId = authUtils.getCurrentUser().getId();

        RideReportDTO report = reportService.generateUserReport(userId, startDate, endDate);

        return ResponseEntity.ok(report);
    }

    /**
     * Admin: Get aggregated report for all drivers or passengers
     * GET /api/reports/rides/aggregated?startDate=2024-01-01&endDate=2024-01-31&userType=DRIVER
     */
    @GetMapping("/aggregated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AggregatedReportDTO> getAggregatedReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String userType) {

        RideReportRequest request = RideReportRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .userType(userType)
                .build();

        AggregatedReportDTO report = reportService.generateAggregatedReport(request);

        return ResponseEntity.ok(report);
    }

    /**
     * Admin: Get report for specific user
     * GET /api/reports/rides/user/123?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RideReportDTO> getUserReport(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        RideReportDTO report = reportService.generateUserReport(userId, startDate, endDate);

        return ResponseEntity.ok(report);
    }
}