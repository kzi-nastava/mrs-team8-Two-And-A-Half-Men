package com.project.mobile.DTO.profile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String email;
    private String imgSrc;
    private String model;
    private String licensePlate;
    private Integer numberOfSeats;
    private Long vehicleTypeId;
    private List<Long> additionalServiceIds;
}
