package com.project.mobile.map.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.NominatimResult;
import com.project.mobile.map.API.NominativApi;
import com.project.mobile.map.API.NominativeClient;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SheredLocationViewModel extends ViewModel {

    private NominativApi api = NominativeClient.create();

    private final MutableLiveData<List<NominatimResult>> suggestions =
            new MutableLiveData<>(new ArrayList<>());

   private final MutableLiveData<List<NominatimResult>> stops =
            new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<NominatimResult>> getSuggestions() { return suggestions; }

    public LiveData<List<NominatimResult>> getStops() { return stops; }

    public void addLocation(GeoPoint location) {
        Log.d("SheredLocations", "Adding location: " + location);
        api.reverse(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), "json").enqueue(new retrofit2.Callback<NominatimResult>() {
            @Override
            public void onResponse(Call<NominatimResult> call, Response<NominatimResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SheredLocations", "Reverse geocoding successful: " + response.body());
                    NominatimResult result = response.body();
                    List<NominatimResult> currentStops = stops.getValue();
                    if (currentStops != null) {
                        currentStops.add(result);
                        stops.postValue(currentStops);
                    }
                }
            }

            @Override
            public void onFailure(Call<NominatimResult> call, Throwable t) {
                // Handle failure
            }
        });
    }
    public void addLocation(NominatimResult location) {
        List<NominatimResult> currentStops = stops.getValue();
        if (currentStops != null) {
            currentStops.add(location);
            stops.postValue(currentStops);
        }
    }
    public void searchLocations(String query) {
        if (query.isEmpty() || query.length() < 3) {
            suggestions.postValue(new ArrayList<>());
            return;
        }
        api.search(query, "json", 5).enqueue(new retrofit2.Callback<List<NominatimResult>>() {
            @Override
            public void onResponse(Call<List<NominatimResult>> call, Response<List<NominatimResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestions.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResult>> call, Throwable t) {
                // Handle failure
            }
        });
    }
    public void clearSuggestions() {
        suggestions.postValue(new ArrayList<>());
    }
    public void RemoveLocation(int index) {
        List<NominatimResult> currentStops = stops.getValue();
        if (currentStops != null && index >= 0 && index < currentStops.size()) {
            currentStops.remove(index);
            stops.postValue(currentStops);
        }
    }
    public void clearStops() {
        stops.postValue(new ArrayList<>());
    }



}
