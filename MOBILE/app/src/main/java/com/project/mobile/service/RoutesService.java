package com.project.mobile.service;

import com.project.mobile.DTO.routes.FavouriteRoute;
import com.project.mobile.DTO.routes.FavouriteRoutesResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Service for managing favourite routes
 */
public interface RoutesService {
    
    @GET("routes/favourites")
    Call<FavouriteRoutesResponse> getFavouriteRoutes();
    
    @POST("routes/{routeId}/favourites")
    Call<Void> addToFavourites(@Path("routeId") long routeId);
    
    @DELETE("routes/{routeId}/favourites")
    Call<Void> removeFromFavourites(@Path("routeId") long routeId);
}
