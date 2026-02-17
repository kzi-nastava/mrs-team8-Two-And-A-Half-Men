package com.project.mobile.service;

import com.project.mobile.DTO.vehicles.PricingUpdate;
import com.project.mobile.DTO.vehicles.VehicleOptions;
import com.project.mobile.DTO.vehicles.VehicleType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface VehicleService {
    @GET("vehicles/options")
    Call<VehicleOptions> getVehicleOptions();

    @GET("vehicle-types")
    Call<List<VehicleType>> getVehicleTypes();

    @PATCH("vehicle-types/{id}/price")
    Call<VehicleType> updateVehiclePrice(
            @Path("id") long id,
            @Body PricingUpdate pricingUpdate
    );
}
