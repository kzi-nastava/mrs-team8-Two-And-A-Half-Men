package com.project.mobile.DTO.routes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response DTO for getting favourite routes
 */
public class FavouriteRoutesResponse {
    
    @SerializedName("routes")
    private List<FavouriteRoute> routes;

    // Constructors
    public FavouriteRoutesResponse() {
    }

    public FavouriteRoutesResponse(List<FavouriteRoute> routes) {
        this.routes = routes;
    }

    // Getters and Setters
    public List<FavouriteRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<FavouriteRoute> routes) {
        this.routes = routes;
    }
}
