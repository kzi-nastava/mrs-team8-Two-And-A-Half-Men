package com.project.backend.DTO.Route;

import com.project.backend.models.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavouriteRouteItemDTO {
    private String address;
    private Double latitude;
    private Double longitude;

    public FavouriteRouteItemDTO(Location location) {
        this.address = location.getAddress();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
}
