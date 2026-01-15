package com.project.backend.DTO.Profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.backend.models.AdditionalService;
import com.project.backend.models.UpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangeRequestDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private String imgSrc;
    private String model;
    private String licensePlate;
    private int numberOfSeats;
    private String vehicleType;
    private List<String> additionalServices;

    public ChangeRequestDTO(UpdateRequest request) {
        this.id = request.getId();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.address = request.getAddress();
        this.phoneNumber = request.getPhoneNumber();
        this.imgSrc = request.getImgSrc();
        this.model = request.getModel();
        this.licensePlate = request.getLicensePlate();
        this.numberOfSeats = request.getNumberOfSeats();
        this.vehicleType = request.getVehicleType().getTypeName();
        this.additionalServices = request.getAdditionalServices().stream()
                .map(AdditionalService::getName)
                .toList();
    }
}
