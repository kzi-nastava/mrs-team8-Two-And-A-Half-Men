package com.project.backend.service;

import com.project.backend.DTO.Profile.*;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Driver;
import com.project.backend.models.UpdateRequest;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.*;
import com.project.backend.util.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ProfileService {
    private final TokenUtils tokenUtils;
    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final UpdateRequestRepository updateRequestRepository;

    public ProfileService(
            TokenUtils tokenUtils,
            AppUserRepository userRepository,
            VehicleRepository vehicleRepository,
            VehicleTypeRepository vehicleTypeRepository,
            AdditionalServiceRepository additionalServiceRepository,
            UpdateRequestRepository updateRequestRepository) {
        this.tokenUtils = tokenUtils;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.updateRequestRepository = updateRequestRepository;
    }

    public ProfileDTO getProfile(Long userId, UserRole role) {
        ProfileDTO.ProfileDTOBuilder resultBuilder = ProfileDTO.builder();
        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        resultBuilder.personalInfo(new PersonalInfoDTO(user));
        if (role == UserRole.DRIVER) {
            vehicleRepository.findByDriverId(userId).ifPresent(vehicle ->
                    resultBuilder.vehicleInfo(new VehicleInfoDTO(vehicle))
            );
            updateRequestRepository.findByDriverId(userId).ifPresent(request ->
                    resultBuilder.pendingChangeRequest(new ChangeRequestDTO(request))
            );
        }
        return resultBuilder.build();
    }

    public UpdateProfileResponseDTO updateProfile(Long userId, UserRole role, UpdateProfileRequestDTO body) {
        String validateMessage = body.validate();
        if (validateMessage != null) {
            throw new BadRequestException(validateMessage);
        }

        AppUser user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (role == UserRole.DRIVER) {
            return updateDriverProfile((Driver) user, body);
        }

        if (body.getFirstName() != null) {
            user.setFirstName(body.getFirstName());
        }
        if (body.getLastName() != null) {
            user.setLastName(body.getLastName());
        }
        if (body.getPhoneNumber() != null) {
            user.setPhoneNumber(body.getPhoneNumber());
        }
        if (body.getAddress() != null) {
            user.setAddress(body.getAddress());
        }
        if (body.getImgSrc() != null) {
            user.setImgSrc(body.getImgSrc());
        }
        if (body.getEmail() != null) {
            user.setEmail(body.getEmail());
        }
        return createUpdateProfileResponse(user, body.getEmail() != null);
    }


    private UpdateProfileResponseDTO updateDriverProfile(Driver driver, UpdateProfileRequestDTO body) {
        UpdateRequest updateRequest = updateRequestRepository
                .findByDriverId(driver.getId())
                .orElse(UpdateRequest.builder().driver(driver).build());

        // Personal info
        if (body.getFirstName() != null) {
            updateRequest.setFirstName(body.getFirstName());
        }
        if (body.getLastName() != null) {
            updateRequest.setLastName(body.getLastName());
        }
        if (body.getPhoneNumber() != null) {
            updateRequest.setPhoneNumber(body.getPhoneNumber());
        }
        if (body.getAddress() != null) {
            updateRequest.setAddress(body.getAddress());
        }
        if (body.getImgSrc() != null) {
            updateRequest.setImgSrc(body.getImgSrc());
        }
        if (body.getEmail() != null) {
            updateRequest.setEmail(body.getEmail());
        }

        // Vehicle info
        if (body.getModel() != null) {
            updateRequest.setModel(body.getModel());
        }
        if (body.getLicensePlate() != null) {
            updateRequest.setLicensePlate(body.getLicensePlate());
        }
        if (body.getNumberOfSeats() != null) {
            updateRequest.setNumberOfSeats(body.getNumberOfSeats());
        }
        if (body.getVehicleTypeId() != null) {
            var vehicleType = vehicleTypeRepository
                    .findById(body.getVehicleTypeId())
                    .orElseThrow(() -> new BadRequestException("Vehicle type not found"));
            updateRequest.setVehicleType(vehicleType);
        }
        if (body.getAdditionalServiceIds() != null) {
            var additionalServices = additionalServiceRepository.findAllById(body.getAdditionalServiceIds());
            updateRequest.setAdditionalServices(new HashSet<>(additionalServices));
        }

        updateRequestRepository.save(updateRequest);
        return createUpdateProfileResponse(driver, false);
    }

    private UpdateProfileResponseDTO createUpdateProfileResponse(AppUser user, boolean regenerateToken) {
        ProfileDTO profileDTO = getProfile(user.getId(), user.getRole());
        String accessToken = null;
        if (regenerateToken) {
            accessToken = tokenUtils.generateToken(user);
        }
        return new UpdateProfileResponseDTO(accessToken, profileDTO);
    }
}