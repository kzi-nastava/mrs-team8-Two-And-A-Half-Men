package com.project.mobile.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.Ride.CostTimeDTO;
import com.project.mobile.DTO.Ride.FinishRideDTO;
import com.project.mobile.DTO.Ride.NoteRequestDTO;
import com.project.mobile.DTO.Ride.NoteResponseDTO;
import com.project.mobile.DTO.Ride.RatingRequestDTO;
import com.project.mobile.DTO.Ride.RatingResponseDTO;
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
    private MutableLiveData<RatingResponseDTO> ratingResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> ratingSuccess = new MutableLiveData<>();
    private MutableLiveData<NoteResponseDTO> noteResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> startSuccess = new MutableLiveData<>();
    private MutableLiveData<CostTimeDTO> endRideResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> endSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> finishSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> panicSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> resolvePanicSuccess = new MutableLiveData<>();
    public LiveData<Boolean> getStartSuccess() {
        return startSuccess;
    }
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

    public LiveData<Boolean> getResolvePanicSuccess() {
        return resolvePanicSuccess;
    }
    public LiveData<RatingResponseDTO> getRatingResponse() {
        return ratingResponse;
    }
    public LiveData<Boolean> getRatingSuccess() {
        return ratingSuccess;
    }
    public LiveData<CostTimeDTO> getEndRideResult() {
        return endRideResult;
    }
    public LiveData<Boolean> getEndSuccess() {
        return endSuccess;
    }
    public LiveData<Boolean> getFinishSuccess() {
        return finishSuccess;
    }
    public void loadRideById(Long rideId) {
        isLoading.setValue(true);
        error.setValue(null);
        resetActionStates();
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
                    loadRideById(rideId); // Refresh ride details to show new note
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

    public LiveData<Boolean> getPanicSuccess() {
        return panicSuccess;
    }
    public void resolvePanic(Long rideId) {
        isLoading.setValue(true);
        error.setValue(null);
        resolvePanicSuccess.setValue(null);

        Call<Void> call = rideService.resolvePanic(rideId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    Log.d("RESOLVE_PANIC", "Panic alert resolved successfully for ride: " + rideId);
                    resolvePanicSuccess.setValue(true);
                    loadRideById(rideId); // Refresh ride details if needed
                } else {
                    Log.e("RESOLVE_PANIC", "Error: Response code " + response.code());
                    error.setValue("Failed to resolve panic. Error code: " + response.code());
                    resolvePanicSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("RESOLVE_PANIC", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                resolvePanicSuccess.setValue(false);
            }
        });
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
                loadRideById(rideId); // Refresh ride details to reflect cancellation
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
    public void rateRide(Long rideId, String accessToken, Integer driverRating, Integer vehicleRating,  String comment) {
        isLoading.setValue(true);
        error.setValue(null);
        ratingSuccess.setValue(null);

        RatingRequestDTO ratingRequest = new RatingRequestDTO(driverRating, vehicleRating, comment);

        Call<RatingResponseDTO> call = rideService.rateRide(rideId, accessToken, ratingRequest);

        call.enqueue(new Callback<RatingResponseDTO>() {
            @Override
            public void onResponse(Call<RatingResponseDTO> call, Response<RatingResponseDTO> response) {
                isLoading.setValue(false);
                loadRideById(rideId); // Refresh ride details to show updated rating
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RATE_RIDE", "Ride rated successfully: " + rideId);
                    ratingResponse.setValue(response.body());
                    ratingSuccess.setValue(true);
                } else {
                    Log.e("RATE_RIDE", "Error: Response code " + response.code());
                    error.setValue("Failed to rate ride. Error code: " + response.code());
                    ratingSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<RatingResponseDTO> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("RATE_RIDE", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                ratingSuccess.setValue(false);
            }
        });
    }
    public void startRide(Long rideId) {
        isLoading.setValue(true);
        error.setValue(null);
        startSuccess.setValue(null);

        Call<Void> call = rideService.startRide(rideId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                loadRideById(rideId); // Refresh ride details to reflect started status
                if (response.isSuccessful()) {
                    Log.d("START_RIDE", "Ride started successfully: " + rideId);
                    startSuccess.setValue(true);
                } else {
                    error.setValue("Failed to start ride. Error code: " + response.code());
                    startSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                startSuccess.setValue(false);
            }
        });
    }
    public void endRide(Long rideId) {
        isLoading.setValue(true);
        error.setValue(null);
        endSuccess.setValue(null);

        Call<CostTimeDTO> call = rideService.endRide(rideId);

        call.enqueue(new Callback<CostTimeDTO>() {
            @Override
            public void onResponse(Call<CostTimeDTO> call, Response<CostTimeDTO> response) {
                isLoading.setValue(false);
                loadRideById(rideId);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("END_RIDE", "Ride ended successfully: " + rideId);
                    endRideResult.setValue(response.body());
                    endSuccess.setValue(true);
                } else {
                    Log.e("END_RIDE", "Error: Response code " + response.code());
                    error.setValue("Failed to end ride. Error code: " + response.code());
                    endSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<CostTimeDTO> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("END_RIDE", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                endSuccess.setValue(false);
            }
        });
    }

    public void finishRide(Long rideId, FinishRideDTO finishRideDTO) {
        isLoading.setValue(true);
        error.setValue(null);
        finishSuccess.setValue(null);

        Call<Void> call = rideService.finishRide(rideId, finishRideDTO);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                loadRideById(rideId);
                if (response.isSuccessful()) {
                    Log.d("FINISH_RIDE", "Ride finished successfully: " + rideId);
                    finishSuccess.setValue(true);
                } else {
                    Log.e("FINISH_RIDE", "Error: Response code " + response.code());
                    error.setValue("Failed to finish ride. Error code: " + response.code());
                    finishSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                Log.e("FINISH_RIDE", "Network error", t);
                error.setValue("Network error: " + t.getMessage());
                finishSuccess.setValue(false);
            }
        });
    }
    public void resetActionStates() {
        cancelSuccess.setValue(null);
        startSuccess.setValue(null);
        endSuccess.setValue(null);
        finishSuccess.setValue(null);
        panicSuccess.setValue(null);
        resolvePanicSuccess.setValue(null);
        ratingSuccess.setValue(null);
        error.setValue(null);
    }

}

