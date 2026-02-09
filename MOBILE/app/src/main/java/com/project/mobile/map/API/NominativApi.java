package com.project.mobile.map.API;

import com.project.mobile.DTO.NominatimResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominativApi {

  @GET("search")
    Call<List<NominatimResult>> search(@Query("q") String query, @Query("format") String format, @Query("limit") int limit);
  @GET("reverse")
    Call<NominatimResult> reverse(@Query("lat") String lat, @Query("lon") String lon, @Query("format") String format);
}
