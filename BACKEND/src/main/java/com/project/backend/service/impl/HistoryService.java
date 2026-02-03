package com.project.backend.service.impl;

import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IHistoryService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    public PagedResponse<RideResponseDTO> getDriverRideHistory(
            Long driverId,
            Pageable pageable,
            LocalDateTime startDate, LocalDateTime endDate
    ) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver with id " + driverId + " not found"
                ));

        Page<Ride> ridePage;

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate))
                throw new BadRequestException("Start date must be before end date");

            ridePage = rideRepository.findByDriverAndDateRange(driver, startDate, endDate, pageable);
        }

        else if (startDate != null)
            ridePage = rideRepository.findByDriverAndStartDateAfter(driver, startDate, pageable);

        else if (endDate != null)
            ridePage = rideRepository.findByDriverAndEndDateBefore(driver, endDate, pageable);

        else
            ridePage = rideRepository.findByDriver(driver, pageable);

        List<RideResponseDTO> historyDTOs = ridePage
                .getContent()
                .stream()
                .map(RideMapper::convertToRideResponseDTO)
                .toList();

        return PagedResponse.fromPage(historyDTOs, ridePage);
    }
}
