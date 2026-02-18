package com.project.mobile.DTO.routes;

import com.google.gson.annotations.SerializedName;
import com.project.mobile.DTO.Ride.RouteItemDTO;

import java.util.List;

/**
 * Represents a favourite route
 */
public class FavouriteRoute {
    
    @SerializedName("id")
    private long id;
    
    @SerializedName("points")
    private List<RouteItemDTO> points;

    // Constructors
    public FavouriteRoute() {
    }

    public FavouriteRoute(long id, List<RouteItemDTO> points) {
        this.id = id;
        this.points = points;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<RouteItemDTO> getPoints() {
        return points;
    }

    public void setPoints(List<RouteItemDTO> points) {
        this.points = points;
    }
}
