package com.project.mobile.service;

import com.project.mobile.DTO.CostTimeDTO;
import com.project.mobile.DTO.RideBookingParametersDTO;
import com.project.mobile.models.PagedResponse;
import com.project.mobile.models.Ride;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideService {
    @POST("rides/estimates")
    Call<CostTimeDTO> estimateRide(@Body RideBookingParametersDTO rideData);

    @GET("rides/history")
    Call<PagedResponse<Ride>> getRideHistory(
            @Query("page") int page,
            @Query("size") int size,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sort") String sort
    );

    @GET("rides/{id}")
    Call<Ride> getRideDetails(
            @Path("id") Long rideId
    );
}
