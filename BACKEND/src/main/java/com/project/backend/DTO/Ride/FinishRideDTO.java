package com.project.backend.DTO.Ride;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishRideDTO {
    @NotNull(message = "interrupted field is required")
    Boolean isInterrupted;
    Boolean isPayed;
}
