package com.project.mobile.DTO.profile;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleInfo {
    private long id;
    private String type;
    private int numberOfSeats;
    private String model;
    private String licensePlate;
    private List<String> additionalServices;
}
