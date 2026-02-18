package com.project.backend.DTO.Route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavouriteRouteDTO {
    private Long id;
    private List<FavouriteRouteItemDTO> points;
}
