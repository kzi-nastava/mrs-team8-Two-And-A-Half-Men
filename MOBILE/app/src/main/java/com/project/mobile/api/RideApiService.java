package com.project.mobile.api;

import com.project.mobile.models.PagedResponse;
import com.project.mobile.models.Ride;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RideApiService {

    @GET("api/v1/rides/history")
    Call<PagedResponse<Ride>> getRideHistory(
            @Query("page") int page,
            @Query("size") int size,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("sort") String sort
    );
}