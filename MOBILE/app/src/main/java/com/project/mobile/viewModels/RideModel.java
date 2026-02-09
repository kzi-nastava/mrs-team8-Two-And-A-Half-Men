package com.project.mobile.viewModels;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.CostTimeDTO;
import com.project.mobile.DTO.RideBookingParametersDTO;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.RideService;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideModel extends ViewModel {
    private RideService rideService = RetrofitClient.retrofit.create(RideService.class);
    private String errorLiveData = null;

   public String getErrorLiveData() {
        return errorLiveData;
    }
    public CompletableFuture<CostTimeDTO> estimateRide(RideBookingParametersDTO rideData) {
        Call<CostTimeDTO> call = rideService.estimateRide(rideData);
        CompletableFuture<CostTimeDTO> future = new CompletableFuture<>();

        call.enqueue(new Callback<CostTimeDTO>() {
            @Override
            public void onResponse(Call<CostTimeDTO> call, Response<CostTimeDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ESTIMATE_RIDE", "Success: Cost=" + response.body().getCost() + ", Time=" + response.body().getTime());
                    future.complete(response.body());
                } else {
                    Log.e("ESTIMATE_RIDE", "Error: Response code " + response.code());
                    future.complete(null);
                }
            }

            @Override
            public void onFailure(Call<CostTimeDTO> call, Throwable t) {
                Log.e("ESTIMATE_RIDE", "Network error", t);
                future.complete(null);
                errorLiveData = t.getMessage();
            }
        });

        return future;
    }
}
