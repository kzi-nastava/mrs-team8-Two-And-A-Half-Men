package com.project.backend.DTO.Profile;

import com.project.backend.models.AdditionalService;
import com.project.backend.models.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfoDTO {
    private Long id;
    private String type;
    private Integer numberOfSeats;
    private String model;
    private String licensePlate;
    private List<String> additionalServices;

    public VehicleInfoDTO(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.type = vehicle.getVehicleType().getTypeName();
        this.numberOfSeats = vehicle.getNumberOfSeats();
        this.model = vehicle.getModel();
        this.licensePlate = vehicle.getLicensePlate();
        this.additionalServices = vehicle.getAdditionalServices().stream()
                .map(AdditionalService::getName)
                .toList();
    }
}
