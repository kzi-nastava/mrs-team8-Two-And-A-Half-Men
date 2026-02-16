package com.project.mobile.service;

import com.project.mobile.DTO.vehicles.VehicleOptions;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VehicleService {
    @GET("vehicles/options")
    Call<VehicleOptions> getVehicleOptions();
}
