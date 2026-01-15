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
public class ChangeRequestDTO {
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imgSrc;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String model;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String licensePlate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int numberOfSeats;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String vehicleType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> additionalServices;

    public ChangeRequestDTO(UpdateRequest request) {
        this.id = request.getId();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.password = request.getPassword();
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
