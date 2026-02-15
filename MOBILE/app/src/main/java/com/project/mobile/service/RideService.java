package com.project.mobile.service;

import com.project.mobile.DTO.Ride.CostTimeDTO;
import com.project.mobile.DTO.Ride.NoteRequestDTO;
import com.project.mobile.DTO.Ride.NoteResponseDTO;
import com.project.mobile.DTO.Ride.RideDTO;
import com.project.mobile.models.PagedResponse;
import com.project.mobile.models.Ride;
import com.project.mobile.DTO.Ride.RideTrackingDTO;
import com.project.mobile.DTO.Ride.RideCancelationDTO;
import com.project.mobile.DTO.Ride.RideBookingParametersDTO;
import com.project.mobile.DTO.Ride.RideBookedDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
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
    Call<RideDTO> getRideDetails(
            @Path("id") Long rideId
    );
    @GET("rides/booked")
    Call<List<RideBookedDTO>> getBookedRides();

    @GET("rides/tracking/{id}")
    Call<RideTrackingDTO> getRideTrackingById(@Path("id") Long id);

    @GET("rides/me/active")
    Call<RideTrackingDTO> getActiveRideByToken(@Query("accessToken") String accessToken);

    @POST("rides/{id}/notes")
    Call<NoteResponseDTO> addRideNote(
            @Path("id") Long rideId,
            @Query("accessToken") String accessToken,   // optional
            @Body NoteRequestDTO noteRequest
    );
    @POST("rides/panic")
    Call<Void> triggerPanic(@Query("accessToken") String accessToken);

    @PATCH("rides/{id}/cancel")
    Call<Void> cancelRide(@Path("id") Long id, @Body RideCancelationDTO reason);
}
