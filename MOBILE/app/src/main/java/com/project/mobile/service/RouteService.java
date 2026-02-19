package com.project.mobile.service;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Petrovo
 */
public interface RouteService {

    @POST("routes/{id}/favourites")
    Call<Object> addToFavorites(@Path("id") Long routeId);
    @DELETE("routes/{id}/favourites")
    Call<Object> removeFromFavorites(@Path("id") Long routeId);
    @GET("routes/favourites")
    Call<Object> getFavoriteRoutes();
}
