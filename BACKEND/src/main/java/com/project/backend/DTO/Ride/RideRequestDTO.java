package com.project.backend.DTO.Ride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDTO {

    private String vehicleType;
    private ArrayList<String> additionalServices;
    private ArrayList<String> addressesPoints;
    private LocalDateTime scheduledTime;
}
