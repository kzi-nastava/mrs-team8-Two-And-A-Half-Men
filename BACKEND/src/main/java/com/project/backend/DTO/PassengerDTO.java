package com.project.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerDTO {
    private String email;
    private String inconsistencyNote;
    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;
}
