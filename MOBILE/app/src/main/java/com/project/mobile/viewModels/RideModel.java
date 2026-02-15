package com.project.mobile.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.Ride.CostTimeDTO;
import com.project.mobile.DTO.Ride.NoteRequestDTO;
import com.project.mobile.DTO.Ride.NoteResponseDTO;
import com.project.mobile.DTO.Ride.RideBookedDTO;
import com.project.mobile.DTO.Ride.RideBookingParametersDTO;
import com.project.mobile.DTO.Ride.RideCancelationDTO;
import com.project.mobile.DTO.Ride.RideDTO;
import com.project.mobile.DTO.Ride.RideTrackingDTO;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.models.Ride;
import com.project.mobile.service.RideService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideModel extends ViewModel {
    private RideService rideService = RetrofitClient.retrofit.create(RideService.class);
    private String errorLiveData = null;
    private MutableLiveData<List<RideBookedDTO>> bookedRides = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<RideDTO> rideDetails = new MutableLiveData<>();
    private MutableLiveData<RideTrackingDTO> rideTracking = new MutableLiveData<>();
    private MutableLiveData<Boolean> cancelSuccess = new MutableLiveData<>();
    private MutableLiveData<NoteResponseDTO> noteResponse = new MutableLiveData<>();
    public LiveData<NoteResponseDTO> getNoteResponse() {return noteResponse;}
    public LiveData<RideTrackingDTO> getRideTracking() {
        return rideTracking;
    }
    public LiveData<Boolean> getCancelSuccess() {
        return cancelSuccess;
    }
    public LiveData<RideDTO> getRideDetails() {
        return rideDetails;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
    public LiveData<List<RideBookedDTO>> getBookedRides() {
        return bookedRides;
    }
    public String getErrorLiveData() {
        return errorLiveData;
    }


    public void loadRideById(Long rideId) {
        isLoading.setValue(true);
        error.setValue(null);

        Call<RideDTO> call = rideService.getRideDetails(rideId);

        call.enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RIDE_DETAIL", "Success: Loaded ride " + rideId);
                    rideDetails.setValue(response.body());
                } else {
                    Log.e("RIDE_DETAIL", "Error: Response code " + response.code());
                    error.setValue("Failed to load ride details. Error code: " + response.code());
                    rideDetails.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("RIDE_DETAIL", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                rideDetails.setValue(null);
            }
        });
    }

    public void loadRideByAccessToken(String accessToken) {
        isLoading.setValue(true);
        error.setValue(null);

        Call<RideTrackingDTO> call = rideService.getActiveRideByToken(accessToken);

        call.enqueue(new Callback<RideTrackingDTO>() {
            @Override
            public void onResponse(Call<RideTrackingDTO> call, Response<RideTrackingDTO> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RIDE_TRACKING", "Success: Loaded active ride with token");
                    rideTracking.setValue(response.body());
                } else {
                    Log.e("RIDE_TRACKING", "Error: Response code " + response.code());
                    error.setValue("Failed to load ride with access token. Error code: " + response.code());
                    rideTracking.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RideTrackingDTO> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("RIDE_TRACKING", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                rideTracking.setValue(null);
            }
        });
    }
    public void loadBookedRides() {
        isLoading.setValue(true);
        error.setValue(null);

        Call<List<RideBookedDTO>> call = rideService.getBookedRides();

        call.enqueue(new Callback<List<RideBookedDTO>>() {
            @Override
            public void onResponse(Call<List<RideBookedDTO>> call, Response<List<RideBookedDTO>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("BOOKED_RIDES", "Success: Loaded " + response.body().size() + " rides");
                    bookedRides.setValue(response.body());
                } else {
                    Log.e("BOOKED_RIDES", "Error: Response code " + response.code());
                    error.setValue("Failed to load rides. Error code: " + response.code());
                    bookedRides.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<RideBookedDTO>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("BOOKED_RIDES", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                bookedRides.setValue(null);
            }
        });
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

    public void addRideNote(Long rideId, String accessToken, String noteText) {
        isLoading.setValue(true);
        error.setValue(null);

        NoteRequestDTO request = new NoteRequestDTO(noteText);

        Call<NoteResponseDTO> call = rideService.addRideNote(rideId, accessToken, request);

        call.enqueue(new Callback<NoteResponseDTO>() {
            @Override
            public void onResponse(Call<NoteResponseDTO> call, Response<NoteResponseDTO> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ADD_NOTE", "Note added for ride " + rideId);
                    noteResponse.setValue(response.body());
                } else {
                    Log.e("ADD_NOTE", "Error: " + response.code());
                    error.setValue("Failed to add note. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NoteResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("ADD_NOTE", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
            }
        });
    }
    private MutableLiveData<Boolean> panicSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getPanicSuccess() {
        return panicSuccess;
    }

    public void triggerPanic(String accessToken) {
        isLoading.setValue(true);
        error.setValue(null);

        Call<Void> call = rideService.triggerPanic(accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    Log.d("PANIC", "Panic alert triggered successfully");
                    panicSuccess.setValue(true);
                } else {
                    Log.e("PANIC", "Error: Response code " + response.code());
                    error.setValue("Failed to trigger panic. Error code: " + response.code());
                    panicSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("PANIC", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                panicSuccess.setValue(false);
            }
        });
    }
    public void cancelRide(Long rideId, String reason, String cancelledBy) {
        isLoading.setValue(true);
        error.setValue(null);
        cancelSuccess.setValue(null);

        RideCancelationDTO cancelationDTO = new RideCancelationDTO(reason, cancelledBy);

        Call<Void> call = rideService.cancelRide(rideId, cancelationDTO);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    Log.d("CANCEL_RIDE", "Ride cancelled successfully: " + rideId);
                    cancelSuccess.setValue(true);
                } else {
                    Log.e("CANCEL_RIDE", "Error: Response code " + response.code());
                    error.setValue("Failed to cancel ride. Error code: " + response.code());
                    cancelSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("CANCEL_RIDE", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                cancelSuccess.setValue(false);
            }
        });
    }
}
