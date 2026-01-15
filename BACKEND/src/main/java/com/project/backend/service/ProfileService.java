package com.project.backend.service;

import com.project.backend.DTO.Profile.*;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.AppUser;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.*;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final UpdateRequestRepository updateRequestRepository;

    public ProfileService(AppUserRepository userRepository, VehicleRepository vehicleRepository, VehicleTypeRepository vehicleTypeRepository, AdditionalServiceRepository additionalServiceRepository, UpdateRequestRepository updateRequestRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.updateRequestRepository = updateRequestRepository;
    }

    public GetProfileDTO getProfile(Long userId, UserRole role) {
        GetProfileDTO.GetProfileDTOBuilder resultBuilder = GetProfileDTO.builder();
        AppUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        resultBuilder.personalInfo(new PersonalInfoDTO(user));
        if (role == UserRole.DRIVER) {
            vehicleRepository.findByDriverId(userId).ifPresent(vehicle -> {
                resultBuilder.vehicleInfo(new VehicleInfoDTO(vehicle));
            });
            resultBuilder.vehicleTypes(vehicleTypeRepository.findAll().stream()
                    .map(VehicleTypeDTO::new)
                    .toList());
            resultBuilder.additionalServices(additionalServiceRepository.findAll().stream()
                    .map(AdditionalServiceDTO::new)
                    .toList());
            updateRequestRepository.findByDriverId(userId).ifPresent(request -> {
                resultBuilder.pendingChangeRequest(new ChangeRequestDTO(request));
            });
        }
        return resultBuilder.build();
    }
}