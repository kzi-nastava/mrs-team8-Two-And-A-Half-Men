package com.project.backend.service;

import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.models.Customer;
import com.project.backend.models.Ride;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IHistoryService {
    PagedResponse<RideResponseDTO> getDriverRideHistory(Long driverId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);
    PagedResponse<RideResponseDTO> getCustomerRideHistory(Long customerId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);
    PagedResponse<RideResponseDTO> getRideHistoryForUserID(Long userId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);
}
