package com.project.backend.service.impl;

import com.project.backend.DTO.PricingUpdateDTO;
import com.project.backend.DTO.VehicleTypeDTO;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.VehicleType;
import com.project.backend.repositories.VehicleTypeRepository;
import com.project.backend.service.IVehicleTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class VehicleTypeService implements IVehicleTypeService {
    private final VehicleTypeRepository vehicleTypeRepository;

    public List<VehicleTypeDTO> getAllVehicleTypes() {
        return vehicleTypeRepository.findAllDTOs();
    }

    public VehicleTypeDTO getVehicleType(Long id) {
        VehicleType vehicleType = vehicleTypeRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Vehicle type with id " + id + " not found."
                ));

        return convertVehicleTypeToDTO(vehicleType);
    }

    @Transactional
    public VehicleTypeDTO updatePricing(Long id, PricingUpdateDTO pricingUpdateDTO) {
        VehicleType vehicleType = vehicleTypeRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Vehicle type with id " + id + " not found."
                ));

        vehicleType.setPrice(pricingUpdateDTO.getPrice());
        vehicleTypeRepository.save(vehicleType);

        return convertVehicleTypeToDTO(vehicleType);
    }

    private VehicleTypeDTO convertVehicleTypeToDTO(VehicleType vehicleType) {
        return new VehicleTypeDTO(
                vehicleType.getId(),
                vehicleType.getTypeName(),
                vehicleType.getDescription(),
                vehicleType.getPrice()
        );
    }
}
