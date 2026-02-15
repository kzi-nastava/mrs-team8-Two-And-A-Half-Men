package com.project.mobile.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.RideService;
import com.project.mobile.service.RouteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteViewModel extends ViewModel {

    private static final String TAG = "RouteViewModel";
    private final RouteService routeApi = RetrofitClient.retrofit.create(RouteService.class);

    private final MutableLiveData<Boolean> favoriteStatus = new MutableLiveData<>();
    private final MutableLiveData<Object> favoriteRoutes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();



    public void addToFavorites(Long routeId) {
        Call<Object> call = routeApi.addToFavorites(routeId);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Route added to favorites successfully");
                    favoriteStatus.postValue(true);
                } else {
                    Log.e(TAG, "Failed to add to favorites: " + response.code());
                    error.postValue("Failed to add to favorites: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Error adding to favorites", t);
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void removeFromFavorites(Long routeId) {
        Call<Object> call = routeApi.removeFromFavorites(routeId);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Route removed from favorites successfully");
                    favoriteStatus.postValue(false);
                } else {
                    Log.e(TAG, "Failed to remove from favorites: " + response.code());
                    error.postValue("Failed to remove from favorites: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Error removing from favorites", t);
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void getFavoriteRoutesViewModel() {
        Call<Object> call = routeApi.getFavoriteRoutes();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Favorite routes retrieved successfully");
                    favoriteRoutes.postValue(response.body());
                } else {
                    Log.e(TAG, "Failed to get favorites: " + response.code());
                    error.postValue("Failed to get favorites: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Error getting favorites", t);
                error.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public LiveData<Boolean> getFavoriteStatus() {
        return favoriteStatus;
    }

    public LiveData<Object> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public LiveData<String> getError() {
        return error;
    }
}