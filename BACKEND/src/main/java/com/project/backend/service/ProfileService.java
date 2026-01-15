package com.project.backend.service;

import com.project.backend.DTO.Profile.*;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.AppUser;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.AdditionalServiceRepository;
import com.project.backend.repositories.AppUserRepository;
import com.project.backend.repositories.VehicleRepository;
import com.project.backend.repositories.VehicleTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;

    public ProfileService(AppUserRepository userRepository, VehicleRepository vehicleRepository, VehicleTypeRepository vehicleTypeRepository, AdditionalServiceRepository additionalServiceRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.additionalServiceRepository = additionalServiceRepository;
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
        }
        return resultBuilder.build();
    }
}