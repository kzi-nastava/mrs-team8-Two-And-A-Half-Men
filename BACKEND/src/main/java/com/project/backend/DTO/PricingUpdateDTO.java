package com.project.backend.DTO;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingUpdateDTO {
    @NotNull(message = "Base price is required")
    @Min(value = 0, message = "Base price must be positive")
    private Double price;
}
