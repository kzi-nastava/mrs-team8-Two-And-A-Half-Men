package com.project.backend.service;

import com.project.backend.DTO.Profile.*;
import com.project.backend.events.ProfilePictureUpdatedEvent;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Driver;
import com.project.backend.models.UpdateRequest;
import com.project.backend.models.Vehicle;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.UpdateRequestRepository;
import com.project.backend.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileUpdateRequestService {
    private final UpdateRequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final AppUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<AdminUpdateRequestListItemDTO> getRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(AdminUpdateRequestListItemDTO::new);
    }

    public ProfileDTO getSingleRequest(Long requestId) {
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(("Update request not found")));
        var vehicle =  vehicleRepository
                .findByDriverId(request.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return new ProfileDTO(
                new PersonalInfoDTO(request.getDriver()),
                new VehicleInfoDTO(vehicle),
                new ChangeRequestDTO(request));
    }

    @Transactional
    public Object cancelRequest(Driver driver, Long requestId) {
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(("Update request not found")));
        if (!request.getDriver().getId().equals(driver.getId())) {
            throw new ForbiddenException("You are not allowed to cancel this request");
        }
        if (request.getImgSrc() != null && !request.getImgSrc().startsWith("http")) {
            // Publish an event to delete the old profile picture after the transaction is committed
            eventPublisher.publishEvent(new ProfilePictureUpdatedEvent(
                    Paths.get(System.getProperty("user.dir"), "public", request.getImgSrc()).toString()
            ));
        }
        requestRepository.delete(request);
        return Map.of("ok", true);
    }

    @Transactional
    public Object rejectRequest(Long requestId) {
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(("Update request not found")));
        if (request.getImgSrc() != null && !request.getImgSrc().startsWith("http")) {
            // Publish an event to delete the old profile picture after the transaction is committed
            eventPublisher.publishEvent(new ProfilePictureUpdatedEvent(
                    Paths.get(System.getProperty("user.dir"), "public", request.getImgSrc()).toString()
            ));
        }
        requestRepository.delete(request);
        return Map.of("ok", true);
    }

    @Transactional(rollbackFor = Exception.class)
    public Object approveRequest(Long requestId) {
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(("Update request not found")));

        var driver = request.getDriver();

        if (driver.getImgSrc() != null && !driver.getImgSrc().startsWith("http")) {
            // Publish an event to delete the old profile picture after the transaction is committed
            eventPublisher.publishEvent(new ProfilePictureUpdatedEvent(
                    Paths.get(System.getProperty("user.dir"), "public", driver.getImgSrc()).toString()
            ));
        }


        driver = getUpdatedDriver(request);
        var vehicle = getUpdatedVehicle(request);

        userRepository.save(driver);
        vehicleRepository.save(vehicle);
        requestRepository.delete(request);
        return Map.of("ok", true);
    }

    private Vehicle getUpdatedVehicle(UpdateRequest request) {
        var vehicle = vehicleRepository
                .findByDriverId(request.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (request.getModel() != null) {
            vehicle.setModel(request.getModel());
        }
        if (request.getLicensePlate() != null) {
            vehicle.setLicensePlate(request.getLicensePlate());
        }
        if (request.getNumberOfSeats() != null && request.getNumberOfSeats() > 0) {
            vehicle.setNumberOfSeats(request.getNumberOfSeats());
        }
        if (request.getVehicleType() != null) {
            vehicle.setVehicleType(request.getVehicleType());
        }
        if (request.getAdditionalServices() != null) {
            vehicle.setAdditionalServices(request.getAdditionalServices());
        }
        return vehicle;
    }

    private Driver getUpdatedDriver(UpdateRequest request) {
        var driver = request.getDriver();
        if (request.getFirstName() != null) {
            driver.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            driver.setLastName(request.getLastName());
        }
        if (request.getAddress() != null) {
            driver.setAddress(request.getAddress());
        }
        if (request.getEmail() != null) {
            driver.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            driver.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getImgSrc() != null) {
            driver.setImgSrc(request.getImgSrc());
        }
        return driver;
    }
}
