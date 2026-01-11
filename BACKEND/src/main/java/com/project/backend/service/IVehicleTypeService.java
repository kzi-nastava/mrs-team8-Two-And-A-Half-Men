package com.project.backend.service;

import com.project.backend.DTO.PricingUpdateDTO;
import com.project.backend.DTO.VehicleTypeDTO;

import java.util.List;

public interface IVehicleTypeService {
    List<VehicleTypeDTO> getAllVehicleTypes();

    VehicleTypeDTO getVehicleType(Long id);

    VehicleTypeDTO updatePricing(Long id, PricingUpdateDTO pricingUpdateDTO);
}
