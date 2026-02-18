package com.project.backend.DTO.Route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteItemDTO {
    private String address;
    private double latitude;
    private double longitude;
}
