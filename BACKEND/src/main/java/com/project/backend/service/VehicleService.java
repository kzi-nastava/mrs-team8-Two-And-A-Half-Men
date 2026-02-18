package com.project.backend.service;

import com.project.backend.DTO.Vehicle.AdditionalServiceDTO;
import com.project.backend.DTO.Vehicle.OptionsDTO;
import com.project.backend.DTO.Vehicle.VehicleTypeDTO;
import com.project.backend.repositories.AdditionalServiceRepository;
import com.project.backend.repositories.VehicleTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final AdditionalServiceRepository additionalServiceRepository;

    public VehicleService(VehicleTypeRepository vehicleTypeRepository, AdditionalServiceRepository additionalServiceRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.additionalServiceRepository = additionalServiceRepository;
    }

    public OptionsDTO getVehicleOptions() {
        OptionsDTO.OptionsDTOBuilder resultBuilder = OptionsDTO.builder();
        resultBuilder.vehicleTypes(
                vehicleTypeRepository.findAll().stream()
                        .map(VehicleTypeDTO::new)
                        .toList()
        );
        resultBuilder.additionalServices(
                additionalServiceRepository.findAll().stream()
                        .map(AdditionalServiceDTO::new)
                        .toList()
        );
        return resultBuilder.build();
    }
}
