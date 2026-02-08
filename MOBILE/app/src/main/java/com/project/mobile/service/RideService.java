package com.project.mobile.service;

import com.project.mobile.DTO.CostTimeDTO;
import com.project.mobile.DTO.RideBookingParametersDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideService {
    @POST("rides/estimates")
    Call<CostTimeDTO> estimateRide(@Body RideBookingParametersDTO rideData);
    
}
