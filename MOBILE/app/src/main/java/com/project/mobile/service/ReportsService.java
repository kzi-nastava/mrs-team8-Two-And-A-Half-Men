package com.project.mobile.service;

import com.project.mobile.DTO.reports.AggregatedReportDTO;
import com.project.mobile.DTO.reports.RideReportDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportsService {

    @GET("reports/rides/my-report")
    Call<RideReportDTO> getMyReport(
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );

    @GET("reports/rides/aggregated")
    Call<AggregatedReportDTO> getAggregatedReport(
        @Query("startDate") String startDate,
        @Query("endDate") String endDate,
        @Query("userType") String userType
    );

    @GET("reports/rides/user/{userId}")
    Call<RideReportDTO> getUserReport(
        @Path("userId") long userId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );
}
