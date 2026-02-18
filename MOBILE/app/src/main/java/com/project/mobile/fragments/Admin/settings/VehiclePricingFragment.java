package com.project.mobile.fragments.Admin.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.vehicles.PricingUpdate;
import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.VehicleService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiclePricingFragment extends Fragment implements VehiclePricingAdapter.PricingListener {

    private VehicleService vehicleService;
    private VehiclePricingAdapter adapter;

    private RecyclerView recyclerView;
    private LinearLayout layoutLoading;
    private LinearLayout layoutError;
    private LinearLayout layoutContent;
    private TextView tvErrorMessage;
    private Button btnRefresh;
    private Button btnRetry;

    public static VehiclePricingFragment newInstance() {
        return new VehiclePricingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicleService = RetrofitClient.retrofit.create(VehicleService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicle_pricing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadVehicleTypes();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_vehicle_types);
        layoutLoading = view.findViewById(R.id.layout_loading);
        layoutError = view.findViewById(R.id.layout_error);
        layoutContent = view.findViewById(R.id.layout_content);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnRetry = view.findViewById(R.id.btn_retry);

        btnRefresh.setOnClickListener(v -> loadVehicleTypes());
        btnRetry.setOnClickListener(v -> loadVehicleTypes());
    }

    private void setupRecyclerView() {
        adapter = new VehiclePricingAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerView.setAdapter(adapter);
    }

    private void loadVehicleTypes() {
        showLoading();

        vehicleService.getVehicleTypes().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<VehicleType>> call,
                                   @NonNull Response<List<VehicleType>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    adapter.setVehicleTypes(response.body());
                    showContent();
                } else {
                    showError(getString(R.string.error_loading_vehicle_types));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<VehicleType>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                showError(getString(R.string.error_loading_vehicle_types) + ": " + t.getMessage());
            }
        });
    }

    @Override
    public void onSavePrice(VehicleType vehicleType, double newPrice) {
        if (newPrice < 0) {
            Toast.makeText(requireContext(), R.string.error_invalid_price, Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPrice == vehicleType.getPrice()) {
            adapter.setEditingId(-1);
            return;
        }

        adapter.setSavingId(vehicleType.getId());

        PricingUpdate update = new PricingUpdate(newPrice);

        vehicleService.updateVehiclePrice(vehicleType.getId(), update)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<VehicleType> call,
                                           @NonNull Response<VehicleType> response) {
                        if (!isAdded()) return;

                        adapter.setSavingId(-1);

                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updateItem(response.body());
                            adapter.setEditingId(-1);
                            Toast.makeText(requireContext(),
                                    vehicleType.getTypeName() + " " + getString(R.string.success_price_updated),
                                    Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 403) {
                            Toast.makeText(requireContext(), R.string.error_no_permission, Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(requireContext(), R.string.error_vehicle_type_not_found, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), R.string.error_updating_price, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VehicleType> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        adapter.setSavingId(-1);
                        Toast.makeText(requireContext(),
                                getString(R.string.error_updating_price) + ": " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        layoutContent.setVisibility(View.GONE);
    }

    private void showContent() {
        layoutLoading.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        layoutLoading.setVisibility(View.GONE);
        layoutContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }
}