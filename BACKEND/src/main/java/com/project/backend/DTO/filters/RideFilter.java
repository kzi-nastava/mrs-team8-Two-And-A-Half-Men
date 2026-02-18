package com.project.backend.DTO.filters;

import com.project.backend.models.enums.RideStatus;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Data
public class RideFilter {
    private Long userId;
    private Long driverId;
    private Long customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RideStatus status;

    private Long vehicleTypeId;
    private Double minPrice;
    private Double maxPrice;
    private Double minDistance;
    private Double maxDistance;
    private Boolean isScheduled;
    private Boolean isCancelled;

    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";

    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, size, sort);
    }
}