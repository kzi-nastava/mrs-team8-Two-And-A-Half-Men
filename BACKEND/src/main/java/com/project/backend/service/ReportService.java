package com.project.backend.service;

import com.project.backend.DTO.reports.*;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final RideRepository rideRepository;
    private final AppUserRepository appUserRepository;

    /**
     * Generate report for current user (driver or passenger)
     */
    public RideReportDTO generateUserReport(Long userId, LocalDate startDate, LocalDate endDate) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateDateRange(startDate, endDate);

        List<Ride> rides;

        if (user instanceof Driver) {
            rides = rideRepository.findCompletedRidesByDriverAndDateRange(
                    userId,
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()
            );
        } else if (user instanceof Customer) {
            rides = rideRepository.findCompletedRidesByPassengerAndDateRange(
                    userId,
                    startDate.atStartOfDay(),
                    endDate.plusDays(1).atStartOfDay()
            );
        } else {
            throw new BadRequestException("Invalid user type");
        }

        return generateReportFromRides(rides, startDate, endDate, user instanceof Driver);
    }

    /**
     * Admin: Generate aggregated report for all drivers or passengers
     */
    public AggregatedReportDTO generateAggregatedReport(RideReportRequest request) {

        validateDateRange(request.getStartDate(), request.getEndDate());

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().plusDays(1).atStartOfDay();

        List<AggregatedUserReportDTO> userReports = new ArrayList<>();
        List<Ride> allRides;

        if ("DRIVER".equalsIgnoreCase(request.getUserType())) {
            allRides = generateDriverReports(userReports, startDateTime, endDateTime, request);
        } else if ("PASSENGER".equalsIgnoreCase(request.getUserType())) {
            allRides = generatePassengerReports(userReports, startDateTime, endDateTime, request);
        } else {
            throw new BadRequestException("Invalid userType. Must be 'DRIVER' or 'PASSENGER'");
        }

        // Generate combined stats
        RideReportDTO combinedStats = generateReportFromRides(
                allRides, request.getStartDate(), request.getEndDate(),
                "DRIVER".equalsIgnoreCase(request.getUserType())
        );

        return AggregatedReportDTO.builder()
                .userReports(userReports)
                .combinedStats(combinedStats)
                .build();
    }

    /**
     * Generate reports for all drivers
     */
    private List<Ride> generateDriverReports(
            List<AggregatedUserReportDTO> userReports,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            RideReportRequest request) {

        List<Ride> allRides = new ArrayList<>();
        List<Long> driverIds = rideRepository.findDriverIdsWithRidesInDateRange(
                startDateTime, endDateTime
        );

        for (Long driverId : driverIds) {
            try {
                AppUser driver = appUserRepository.findById(driverId).orElse(null);

                if (driver == null) {
                    log.warn("Driver with ID {} not found, skipping", driverId);
                    continue;
                }

                List<Ride> driverRides = rideRepository.findCompletedRidesByDriverAndDateRange(
                        driverId, startDateTime, endDateTime
                );

                // Filter out rides with null driver (safety check)
                driverRides = driverRides.stream()
                        .filter(ride -> ride.getDriver() != null)
                        .collect(Collectors.toList());

                allRides.addAll(driverRides);

                RideReportDTO report = generateReportFromRides(
                        driverRides, request.getStartDate(), request.getEndDate(), true
                );

                userReports.add(AggregatedUserReportDTO.builder()
                        .userId(driverId)
                        .userName(getUserName(driver))
                        .userEmail(driver.getEmail() != null ? driver.getEmail() : "N/A")
                        .report(report)
                        .build());

            } catch (Exception e) {
                log.error("Error generating report for driver {}: {}", driverId, e.getMessage());
            }
        }

        return allRides;
    }

    /**
     * Generate reports for all passengers
     */
    private List<Ride> generatePassengerReports(
            List<AggregatedUserReportDTO> userReports,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            RideReportRequest request) {

        List<Ride> allRides = new ArrayList<>();
        List<Long> passengerIds = rideRepository.findPassengerIdsWithRidesInDateRange(
                startDateTime, endDateTime
        );

        for (Long passengerId : passengerIds) {
            try {
                AppUser passenger = appUserRepository.findById(passengerId).orElse(null);

                if (passenger == null) {
                    log.warn("Passenger with ID {} not found, skipping", passengerId);
                    continue;
                }

                List<Ride> passengerRides = rideRepository.findCompletedRidesByPassengerAndDateRange(
                        passengerId, startDateTime, endDateTime
                );

                allRides.addAll(passengerRides);

                RideReportDTO report = generateReportFromRides(
                        passengerRides, request.getStartDate(), request.getEndDate(), false
                );

                userReports.add(AggregatedUserReportDTO.builder()
                        .userId(passengerId)
                        .userName(getUserName(passenger))
                        .userEmail(passenger.getEmail() != null ? passenger.getEmail() : "N/A")
                        .report(report)
                        .build());

            } catch (Exception e) {
                log.error("Error generating report for passenger {}: {}", passengerId, e.getMessage());
            }
        }

        return allRides;
    }

    /**
     * Admin: Generate report for a specific user
     */
    public RideReportDTO generateAdminUserReport(RideReportRequest request) {
        if (request.getUserId() == null) {
            throw new BadRequestException("User ID is required");
        }
        return generateUserReport(request.getUserId(), request.getStartDate(), request.getEndDate());
    }

    /**
     * Core method to generate report from list of rides
     */
    private RideReportDTO generateReportFromRides(
            List<Ride> rides,
            LocalDate startDate,
            LocalDate endDate,
            boolean isDriver) {

        // Filter out rides with null endTime
        rides = rides.stream()
                .filter(ride -> ride.getEndTime() != null)
                .collect(Collectors.toList());

        // Group rides by date
        Map<LocalDate, List<Ride>> ridesByDate = rides.stream()
                .collect(Collectors.groupingBy(ride -> ride.getEndTime().toLocalDate()));

        // Generate daily stats
        List<DailyRideStats> dailyStats = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            List<Ride> dayRides = ridesByDate.getOrDefault(currentDate, Collections.emptyList());

            long numberOfRides = dayRides.size();
            double totalDistance = dayRides.stream()
                    .mapToDouble(ride -> ride.getDistanceKm() != null ? ride.getDistanceKm() : 0.0)
                    .sum();
            double totalAmount = dayRides.stream()
                    .mapToDouble(ride -> {
                        if (isDriver) {
                            // For drivers: money earned (price)
                            return ride.getPrice() != null ? ride.getPrice() : 0.0;
                        } else {
                            // For passengers: money spent (totalCost)
                            return ride.getTotalCost() != null ? ride.getTotalCost() : 0.0;
                        }
                    })
                    .sum();

            dailyStats.add(DailyRideStats.builder()
                    .date(currentDate)
                    .numberOfRides(numberOfRides)
                    .totalDistance(totalDistance)
                    .totalAmount(totalAmount)
                    .build());

            currentDate = currentDate.plusDays(1);
        }

        // Calculate cumulative totals
        long totalRides = dailyStats.stream()
                .mapToLong(DailyRideStats::getNumberOfRides)
                .sum();

        double totalDistance = dailyStats.stream()
                .mapToDouble(DailyRideStats::getTotalDistance)
                .sum();

        double totalAmount = dailyStats.stream()
                .mapToDouble(DailyRideStats::getTotalAmount)
                .sum();

        // Calculate number of days in range
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Calculate averages (avoid division by zero)
        double averageRidesPerDay = numberOfDays > 0 ? (double) totalRides / numberOfDays : 0.0;
        double averageDistancePerDay = numberOfDays > 0 ? totalDistance / numberOfDays : 0.0;
        double averageAmountPerDay = numberOfDays > 0 ? totalAmount / numberOfDays : 0.0;
        double averageDistancePerRide = totalRides > 0 ? totalDistance / totalRides : 0.0;
        double averageAmountPerRide = totalRides > 0 ? totalAmount / totalRides : 0.0;

        return RideReportDTO.builder()
                .dailyStats(dailyStats)
                .totalRides(totalRides)
                .totalDistance(totalDistance)
                .totalAmount(totalAmount)
                .averageRidesPerDay(averageRidesPerDay)
                .averageDistancePerDay(averageDistancePerDay)
                .averageAmountPerDay(averageAmountPerDay)
                .averageDistancePerRide(averageDistancePerRide)
                .averageAmountPerRide(averageAmountPerRide)
                .build();
    }

    /**
     * Helper method to safely get user's full name
     */
    private String getUserName(AppUser user) {
        if (user == null) {
            return "Unknown User";
        }

        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        String fullName = (firstName + " " + lastName).trim();

        return fullName.isEmpty() ? "Unknown User" : fullName;
    }

    /**
     * Validate date range
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        // Optional: Limit the range to prevent performance issues
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 365) {
            throw new BadRequestException("Date range cannot exceed 365 days");
        }
    }
}