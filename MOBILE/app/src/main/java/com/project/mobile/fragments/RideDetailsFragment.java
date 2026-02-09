package com.project.mobile.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.mobile.DTO.Map.MarkerPointIcon;
import com.project.mobile.R;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.databinding.FragmentRideDetailsBinding;
import com.project.mobile.map.MapFragment;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.RouteDrawer;
import com.project.mobile.models.Ride;
import com.project.mobile.service.RideService;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailsFragment extends Fragment {
    private static final String TAG = "RideDetailsFragment";
    private static final String ARG_RIDE_ID = "rideId";

    private FragmentRideDetailsBinding binding;
    private final RideService rideService = RetrofitClient.retrofit.create(RideService.class);
    private Long rideId;
    private Ride currentRide;

    private RouteDrawer routeDrawer;
    private MarkerDrawer markerDrawer;

    public static RideDetailsFragment newInstance(Long rideId) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRideDetailsBinding.inflate(inflater, container, false);

        setupViewModels();
        setupBackButton();
        loadRideDetails();

        return binding.getRoot();
    }

    private void setupViewModels() {
        routeDrawer = new ViewModelProvider(requireActivity()).get(RouteDrawer.class);
        markerDrawer = new ViewModelProvider(requireActivity()).get(MarkerDrawer.class);
    }

    private void setupBackButton() {
        binding.backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.retryButton.setOnClickListener(v -> loadRideDetails());
    }

    private void loadRideDetails() {
        if (rideId == null) {
            showError("Invalid ride ID");
            return;
        }

        showLoading(true);

        Call<Ride> call = rideService.getRideDetails(rideId);
        call.enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentRide = response.body();
                    displayRideDetails(currentRide);
                    setupMap();
                } else {
                    showError("Failed to load ride details (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Error loading ride details", t);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayRideDetails(Ride ride) {
        binding.contentContainer.setVisibility(View.VISIBLE);

        // Status
        binding.statusBadge.setText(ride.getStatus());
        setStatusColor(ride.getStatus());

        // Panic indicator
        if ("PANICKED".equals(ride.getStatus())) {
            binding.panicIndicator.setVisibility(View.VISIBLE);
        }

        // Basic Information
        binding.rideIdText.setText(String.valueOf(ride.getId()));

        if (ride.getAddresses() != null && !ride.getAddresses().isEmpty()) {
            binding.startPointText.setText(ride.getAddresses().get(0));
            binding.destinationText.setText(ride.getAddresses().get(ride.getAddresses().size() - 1));
        }

        // Times
        if (ride.getScheduledTime() != null && !ride.getScheduledTime().isEmpty()) {
            binding.scheduledTimeText.setText(formatDateTime(ride.getScheduledTime()));
            binding.scheduledTimeContainer.setVisibility(View.VISIBLE);
        } else {
            binding.scheduledTimeContainer.setVisibility(View.GONE);
        }

        if (ride.getStartTime() != null && !ride.getStartTime().isEmpty()) {
            binding.startTimeText.setText(formatDateTime(ride.getStartTime()));
            binding.startTimeContainer.setVisibility(View.VISIBLE);
        } else {
            binding.startTimeContainer.setVisibility(View.GONE);
        }

        if (ride.getEndTime() != null && !ride.getEndTime().isEmpty()) {
            binding.endTimeText.setText(formatDateTime(ride.getEndTime()));
            binding.endTimeContainer.setVisibility(View.VISIBLE);
        } else {
            binding.endTimeContainer.setVisibility(View.GONE);
        }

        // Costs
        binding.priceText.setText(String.format(Locale.getDefault(), "%.2f RSD", ride.getPrice()));
        binding.totalCostText.setText(String.format(Locale.getDefault(), "%.2f RSD", ride.getTotalCost()));

        // People
        binding.driverNameText.setText(ride.getDriverName());
        binding.rideOwnerText.setText(ride.getRideOwnerName());

        // Passengers
        binding.passengersChipGroup.removeAllViews();
        if (ride.getPassengersMails() != null && !ride.getPassengersMails().isEmpty()) {
            for (String passenger : ride.getPassengersMails()) {
                Chip chip = new Chip(requireContext());
                chip.setText(passenger);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_text));
                binding.passengersChipGroup.addView(chip);
            }
        }

        // Additional Services
        if (ride.getAdditionalServices() != null && !ride.getAdditionalServices().isEmpty()) {
            binding.servicesCard.setVisibility(View.VISIBLE);
            binding.servicesChipGroup.removeAllViews();

            for (String service : ride.getAdditionalServices()) {
                Chip chip = new Chip(requireContext());
                chip.setText(service);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_text));
                binding.servicesChipGroup.addView(chip);
            }
        } else {
            binding.servicesCard.setVisibility(View.GONE);
        }

        // Cancellation
        if ("CANCELLED".equals(ride.getStatus()) &&
                ride.getCancellationReason() != null &&
                !ride.getCancellationReason().isEmpty()) {
            binding.cancellationCard.setVisibility(View.VISIBLE);
            binding.cancellationReasonText.setText(ride.getCancellationReason());
        } else {
            binding.cancellationCard.setVisibility(View.GONE);
        }

        // Display all addresses
        displayAddressList(ride.getAddresses());
    }

    private void displayAddressList(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return;
        }

        // Add addresses section to the UI
        binding.addressesCard.setVisibility(View.VISIBLE);
        binding.addressesContainer.removeAllViews();

        for (int i = 0; i < addresses.size(); i++) {
            View addressView = getLayoutInflater().inflate(
                    R.layout.item_addresses,
                    binding.addressesContainer,
                    false
            );

            TextView numberText = addressView.findViewById(R.id.addressNumber);
            TextView addressText = addressView.findViewById(R.id.addressText);
            View markerIcon = addressView.findViewById(R.id.markerIcon);

            if (i == 0) {
                // Start point
                numberText.setText("A");
                markerIcon.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.marker_start)
                        )
                );
            } else if (i == addresses.size() - 1) {
                // End point
                numberText.setText("B");
                markerIcon.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.marker_end)
                        )
                );
            } else {
                // Intermediate stops
                numberText.setText(String.valueOf(i));
                markerIcon.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.marker_stop)
                        )
                );
            }

            addressText.setText(addresses.get(i));
            binding.addressesContainer.addView(addressView);
        }
    }

    private void setupMap() {
        if (currentRide == null || currentRide.getPath() == null || currentRide.getPath().isEmpty()) {
            Toast.makeText(getContext(), "No route data available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add map fragment
        MapFragment mapFragment = new MapFragment(false); // false = not clickable
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();

        // Parse and draw route
        try {
            List<GeoPoint> routePoints = parseRoutePath(currentRide.getPath());

            if (!routePoints.isEmpty()) {
                // Create polyline for route
                Polyline polyline = new Polyline();
                polyline.setPoints(routePoints);
                polyline.setWidth(5f);
                polyline.setColor(ContextCompat.getColor(requireContext(), R.color.route_color));

                // Add to route drawer
                routeDrawer.clearRoutes();
                routeDrawer.addRoute(polyline, "ride_route");

                // Add markers for ALL addresses
                addAddressMarkers(routePoints);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing route path", e);
            Toast.makeText(getContext(), "Error displaying route", Toast.LENGTH_SHORT).show();
        }
    }

    private void addAddressMarkers(List<GeoPoint> routePoints) {
        if (currentRide.getAddresses() == null || currentRide.getAddresses().isEmpty()) {
            return;
        }

        markerDrawer.clearMarkers();

        // Assuming addresses correspond to points in the route
        // You might need to geocode addresses to get exact coordinates
        // For now, we'll use evenly distributed points from the route

        int addressCount = currentRide.getAddresses().size();

        if (addressCount == 1) {
            // Only one address
            addMarker(routePoints.get(0), 0, currentRide.getAddresses().get(0), MarkerType.START);
        } else if (addressCount == 2) {
            // Start and end
            addMarker(routePoints.get(0), 0, currentRide.getAddresses().get(0), MarkerType.START);
            addMarker(routePoints.get(routePoints.size() - 1), 1,
                    currentRide.getAddresses().get(1), MarkerType.END);
        } else {
            // Multiple addresses - distribute along route
            for (int i = 0; i < addressCount; i++) {
                int routeIndex;
                MarkerType markerType;

                if (i == 0) {
                    // Start
                    routeIndex = 0;
                    markerType = MarkerType.START;
                } else if (i == addressCount - 1) {
                    // End
                    routeIndex = routePoints.size() - 1;
                    markerType = MarkerType.END;
                } else {
                    // Intermediate stops - distribute evenly
                    float fraction = (float) i / (addressCount - 1);
                    routeIndex = Math.round(fraction * (routePoints.size() - 1));
                    markerType = MarkerType.STOP;
                }

                addMarker(routePoints.get(routeIndex), i,
                        currentRide.getAddresses().get(i), markerType);
            }
        }
    }

    private enum MarkerType {
        START, STOP, END
    }

    private void addMarker(GeoPoint point, int index, String address, MarkerType type) {
        String label;
        int iconRes;

        switch (type) {
            case START:
                label = "A - Start: " + address;
                iconRes = R.drawable.ic_start_marker;
                break;
            case END:
                label = "B - End: " + address;
                iconRes = R.drawable.ic_end_marker;
                break;
            case STOP:
                label = index + " - Stop: " + address;
                iconRes = R.drawable.ic_stop_marker;
                break;
            default:
                label = address;
                iconRes = R.drawable.ic_stop_marker;
        }

        MarkerPointIcon marker = new MarkerPointIcon(
                point.getLatitude(),
                point.getLongitude(),
                label,
                null
        );

        markerDrawer.addMarker(marker);
    }

    private List<GeoPoint> parseRoutePath(String pathJson) {
        List<GeoPoint> points = new ArrayList<>();

        try {
            JsonArray jsonArray = JsonParser.parseString(pathJson).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonArray coord = element.getAsJsonArray();
                if (coord.size() >= 2) {
                    double lat = coord.get(0).getAsDouble();
                    double lon = coord.get(1).getAsDouble();
                    points.add(new GeoPoint(lat, lon));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing path JSON", e);
        }

        return points;
    }

    private void setStatusColor(String status) {
        int colorResId;
        switch (status) {
            case "PENDING":
                colorResId = R.color.status_pending;
                break;
            case "ACCEPTED":
                colorResId = R.color.status_accepted;
                break;
            case "ACTIVE":
                colorResId = R.color.status_active;
                break;
            case "FINISHED":
                colorResId = R.color.status_finished;
                break;
            case "INTERRUPTED":
                colorResId = R.color.status_interrupted;
                break;
            case "CANCELLED":
                colorResId = R.color.status_cancelled;
                break;
            case "PANICKED":
                colorResId = R.color.status_panicked;
                break;
            default:
                colorResId = R.color.status_default;
        }

        int color = ContextCompat.getColor(requireContext(), colorResId);
        binding.statusBadge.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color)
        );
    }

    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return "N/A";
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, inputFormatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(
                    "dd.MM.yyyy HH:mm",
                    Locale.getDefault()
            );
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return isoDateTime;
        }
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.contentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.errorContainer.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        if (binding != null) {
            binding.errorContainer.setVisibility(View.VISIBLE);
            binding.errorText.setText(message);
            binding.contentContainer.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear route and markers
        if (routeDrawer != null) {
            routeDrawer.clearRoutes();
        }
        if (markerDrawer != null) {
            markerDrawer.clearMarkers();
        }
        binding = null;
    }
}