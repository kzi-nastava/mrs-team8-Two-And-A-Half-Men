package com.project.backend.service;

import com.project.backend.DTO.Ride.HistoryRequestDTO;
import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.Ride.RideResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IHistoryService {
    PagedResponse<RideResponseDTO> getDriverRideHistory(Long driverId, Pageable pageable, HistoryRequestDTO historyRequestDTO);
}
