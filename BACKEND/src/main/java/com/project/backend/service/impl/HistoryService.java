package com.project.backend.service.impl;

import com.project.backend.DTO.Utils.PagedResponse;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.*;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.CustomerRepository;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IHistoryService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;

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
    //@Transactional //https://stackoverflow.com/questions/55702642/jpa-lazy-loading-is-not-working-in-spring-boot
    public PagedResponse<RideResponseDTO> getCustomerRideHistory (
            Long customerID,
            Pageable pageable,
            LocalDateTime startDate, LocalDateTime endDate
    )
    {
        Page<Ride> ridePage;
        Customer customer = customerRepository.findById(customerID).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));// Refresh customer to get favorite routes becouse session ended :D
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate))
                throw new BadRequestException("Start date must be before end date");
        }
        List<RideStatus> statuses = List.of(RideStatus.FINISHED, RideStatus.CANCELLED,RideStatus.PANICKED, RideStatus.INTERRUPTED);
        ridePage = rideRepository.findRidesByPassengerCustomerWithFilters(customer, startDate, endDate, statuses, pageable);
        PagedResponse<RideResponseDTO> pagedResponse = new PagedResponse<RideResponseDTO>();
        pagedResponse.setContent(new ArrayList<>()); // Initialize the list
        for(Ride ride : ridePage.getContent()) {
            RideResponseDTO dto = RideMapper.convertToRideResponseDTO(ride);
            if(customer.getFavoriteRoutes().contains(ride.getRoute())) {
                // Mark as favorite route in DTO
            }
            pagedResponse.getContent().add(dto);
        }
        return pagedResponse;
    }

    @Override
    public PagedResponse<RideResponseDTO> getRideHistoryForUserID(Long userId, Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
        AppUser user = appUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(user instanceof Driver) {
            return getDriverRideHistory(userId, pageable, startDate, endDate);
        } else if(user instanceof Customer) {
            return getCustomerRideHistory(userId, pageable, startDate, endDate);
        } else {
            throw new BadRequestException("User is neither Driver nor Customer");
        }
    }
}
