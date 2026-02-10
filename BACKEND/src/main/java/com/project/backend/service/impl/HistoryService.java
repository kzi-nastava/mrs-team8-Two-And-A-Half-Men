package com.project.backend.service.impl;

import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.filters.RideFilter;
import com.project.backend.DTO.filters.RideSpecification;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.models.*;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IHistoryService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService {
    private final RideRepository rideRepository;
    private final RideMapper rideMapper;

    public PagedResponse<RideResponseDTO> getRideHistory(
            RideFilter filter,
            AppUser currentUser
    ) {
        applyUserSpecificFilter(filter, currentUser);
        Specification<Ride> spec = RideSpecification.withFilter(filter);
        Page<Ride> rides = rideRepository.findAll(spec, filter.toPageable());

        List<RideResponseDTO> historyDTOs = rides
                .getContent()
                .stream()
                .map(rideMapper::convertToRideResponseDTO)
                .toList();

        return PagedResponse.fromPage(historyDTOs, rides);
    }

    private void applyUserSpecificFilter(RideFilter filter, AppUser user) {
        if (user instanceof Driver) {
            filter.setDriverId(user.getId());
            filter.setCustomerId(null);
            filter.setUserId(null);
        }
        else if (user instanceof Customer) {
            filter.setCustomerId(user.getId());
            filter.setDriverId(null);
            filter.setUserId(null);
        }
        else if (user instanceof Admin) {
            if (filter.getUserId() != null) {
                filter.setDriverId(null);
                filter.setCustomerId(null);
            }
        }
    }
}
