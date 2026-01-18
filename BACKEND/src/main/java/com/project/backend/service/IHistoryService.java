package com.project.backend.service;

import com.project.backend.DTO.HistoryRequestDTO;
import com.project.backend.DTO.HistoryResponseDTO;
import com.project.backend.DTO.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface IHistoryService {
    PagedResponse<HistoryResponseDTO> getDriverRideHistory(Long driverId, Pageable pageable, HistoryRequestDTO historyRequestDTO);
}
