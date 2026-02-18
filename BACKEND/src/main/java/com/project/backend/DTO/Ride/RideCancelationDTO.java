package com.project.backend.DTO.Ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideCancelationDTO {
    private String reason;
    private String cancelledBy;

}
