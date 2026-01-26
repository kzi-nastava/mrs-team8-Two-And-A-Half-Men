package com.project.backend.service;

import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.Ride.RideResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IHistoryService {
    PagedResponse<RideResponseDTO> getDriverRideHistory(Long driverId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);
}
