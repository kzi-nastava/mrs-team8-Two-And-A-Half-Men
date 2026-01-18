package com.project.backend.service.impl;

import com.project.backend.DTO.HistoryRequestDTO;
import com.project.backend.DTO.HistoryResponseDTO;
import com.project.backend.DTO.PagedResponse;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IHistoryService;
import lombok.RequiredArgsConstructor;
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

    public PagedResponse<HistoryResponseDTO> getDriverRideHistory(
            Long driverId,
            Pageable pageable,
            HistoryRequestDTO historyRequestDTO
    ) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver with id " + driverId + " not found"
                ));

        Page<Ride> ridePage;
        LocalDateTime startDate = historyRequestDTO.getStartDate();
        LocalDateTime endDate = historyRequestDTO.getEndDate();

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

        List<HistoryResponseDTO> historyDTOs = ridePage
                .getContent()
                .stream()
                .map(this::convertToHistoryResponseDTO)
                .toList();

        return createPagedResponse(historyDTOs, ridePage);
    }

    private PagedResponse<HistoryResponseDTO> createPagedResponse(
            List<HistoryResponseDTO> history,
            Page<Ride> ridePage
    ) {
        return PagedResponse.<HistoryResponseDTO>builder()
                .content(history)
                .currentPage(ridePage.getNumber())
                .totalPages(ridePage.getTotalPages())
                .totalElements(ridePage.getTotalElements())
                .pageSize(ridePage.getSize())
                .hasNext(ridePage.hasNext())
                .hasPrevious(ridePage.hasPrevious())
                .build();
    }

    private HistoryResponseDTO convertToHistoryResponseDTO(Ride ride) {
        return HistoryResponseDTO.builder()
                .id(ride.getId())
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .status(ride.getStatus())
                .cancellationReason(ride.getCancellationReason())
                .cost(ride.getTotalCost())
                .path(ride.getPath())
                .build();
    }
}
