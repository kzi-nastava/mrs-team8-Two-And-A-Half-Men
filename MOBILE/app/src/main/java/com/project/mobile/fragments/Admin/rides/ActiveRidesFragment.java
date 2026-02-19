package com.project.mobile.fragments.Admin.rides;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project.mobile.R;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.databinding.FragmentActiveRidesBinding;
import com.project.mobile.fragments.RideDetailsFragmentActive;
import com.project.mobile.models.Ride;
import com.project.mobile.service.RideService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveRidesFragment extends Fragment {

    private static final String TAG = "ActiveRidesFragment";

    private FragmentActiveRidesBinding binding;
    private ActiveRidesAdapter adapter;
    private List<Ride> rideList;
    private final RideService rideService = RetrofitClient.retrofit.create(RideService.class);

    private String currentDriverName = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentActiveRidesBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupSearch();
        setupRefreshButton();
        loadActiveRides();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        rideList = new ArrayList<>();
        adapter = new ActiveRidesAdapter(rideList, this::onRideClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.driverNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                currentDriverName = query.isEmpty() ? null : query;

                binding.driverNameInput.removeCallbacks(searchRunnable);
                binding.driverNameInput.postDelayed(searchRunnable, 400);
            }
        });

        binding.clearSearchButton.setOnClickListener(v -> {
            binding.driverNameInput.setText("");
            currentDriverName = null;
            loadActiveRides();
        });
    }

    private final Runnable searchRunnable = this::loadActiveRides;

    private void setupRefreshButton() {
        binding.refreshButton.setOnClickListener(v -> loadActiveRides());
    }

    private void loadActiveRides() {
        showLoading(true);

        Call<List<Ride>> call = rideService.getActiveRides(currentDriverName);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Ride>> call,
                                   @NonNull Response<List<Ride>> response) {
                showLoading(false);
                if(binding == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Ride> rides = response.body();
                    rideList.clear();
                    rideList.addAll(rides);
                    adapter.notifyDataSetChanged();

                    updateRideCount(rides.size());

                    if (rides.isEmpty()) {
                        binding.emptyStateText.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyStateText.setVisibility(View.GONE);
                    }
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Ride>> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Network error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onRideClick(Ride ride) {
        RideDetailsFragmentActive detailsFragment = RideDetailsFragmentActive.newInstanceWithId(ride.getId());
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view_tag, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateRideCount(int count) {
        if(binding == null) return;
        String label = count + " active ride" + (count != 1 ? "s" : "");
        binding.rideCountText.setText(label);
        binding.rideCountText.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) binding.emptyStateText.setVisibility(View.GONE);
        }
    }

    private void handleError(int code) {
        String errorMsg;
        if (code == 401) {
            errorMsg = "Unauthorized - please login again";
        } else if (code == 403) {
            errorMsg = "Access forbidden";
        } else if (code >= 500) {
            errorMsg = "Server error - please try again later";
        } else {
            errorMsg = "Failed to load active rides";
        }
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.driverNameInput.removeCallbacks(searchRunnable);
        }
        binding = null;
    }
}