package com.project.mobile.fragments.Registered.Rides.controls;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.mobile.DTO.Ride.RideDTO;
import com.project.mobile.R;
import com.project.mobile.viewModels.RouteViewModel;

public class CancelledRideControls extends Fragment {

    private static final String TAG = "RideActionControls";

    private RideDTO ride;
    private String accessToken;
    private boolean rate;
    private boolean abelToOrder;
    private boolean abelToAddToFavorites;
    private Button btnFavorite;
    private Button btnBookAgain;
    private Button btnRate;
    private RouteViewModel routeViewModel;

    public CancelledRideControls() {
    }

    public static CancelledRideControls newInstance(RideDTO ride, String accessToken, boolean rate, boolean abelToOrder) {
        CancelledRideControls fragment = new CancelledRideControls();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("Ride", gson.toJson(ride));
        args.putString("accessToken", accessToken);
        args.putString("rate", String.valueOf(rate));
        args.putString("abelToOrder", String.valueOf(abelToOrder));
        fragment.setArguments(args);
        return fragment;
    }
    public static CancelledRideControls newInstance(RideDTO ride, String accessToken) {

        CancelledRideControls fragment = new CancelledRideControls();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("Ride", gson.toJson(ride));
        args.putString("accessToken", accessToken);
        args.putString("rate", String.valueOf(true));
        args.putString("abelToOrder", String.valueOf(false));
        args.putString("abelToAddToFavorites", String.valueOf(false));
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);

        if (getArguments() != null) {
            // Deserialize the RideDTO object from JSON string
            Gson gson = new Gson();
            String rideJson = getArguments().getString("Ride");
            ride = gson.fromJson(rideJson, RideDTO.class);
            accessToken = getArguments().getString("accessToken");
            rate = Boolean.parseBoolean(getArguments().getString("rate"));
            abelToOrder = Boolean.parseBoolean(getArguments().getString("abelToOrder"));
            abelToAddToFavorites = Boolean.parseBoolean(getArguments().getString("abelToAddToFavorites"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancelled_ride_controls, container, false);

        btnFavorite = view.findViewById(R.id.btn_favorite);
        btnBookAgain = view.findViewById(R.id.btn_book_again);
        btnRate = view.findViewById(R.id.rate);
        // Set initial button state based on favorite status
        updateFavoriteButton();
        btnRate.setVisibility(rate ? View.VISIBLE : View.GONE);
        btnBookAgain.setVisibility(abelToOrder ? View.VISIBLE : View.GONE);
        btnFavorite.setVisibility(abelToAddToFavorites ? View.VISIBLE : View.GONE);
        // Favorite button - toggle add/remove
        btnFavorite.setOnClickListener(v -> {
            if (ride != null && ride.getRouteId() != null) {
                if (ride.isFavourite()) {
                    // Remove from favorites
                    routeViewModel.removeFromFavorites(ride.getRouteId());
                } else {
                    // Add to favorites
                    routeViewModel.addToFavorites(ride.getRouteId());
                }
            } else {
                Toast.makeText(getContext(), "Missing ride information", Toast.LENGTH_SHORT).show();
            }
        });

        btnBookAgain.setOnClickListener(v -> {
            if (ride != null) {
                Log.d(TAG, "Book Again clicked for ride: " + ride.getId());
                Log.d(TAG, "Route ID: " + ride.getRouteId());
                Log.d(TAG, "Ride details: " + new Gson().toJson(ride));
                Toast.makeText(getContext(), "Book Again - Coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        btnRate.setOnClickListener(v -> {
            if (ride != null) {
                Log.d(TAG, "Rate clicked for ride: " + ride.getId());
                RateRideDialogFragment rateDialog = RateRideDialogFragment.newInstance(ride, accessToken);
                rateDialog.show(getParentFragmentManager(), "RateRideDialog");
            }
        });

        // Observe favorite status changes
        routeViewModel.getFavoriteStatus().observe(getViewLifecycleOwner(), isFavorite -> {
            if (isFavorite != null) {
                // Update local ride object
                ride.setFavourite(isFavorite);
                // Update button appearance
                updateFavoriteButton();

                String message = isFavorite ? "Added to favorites!" : "Removed from favorites!";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe errors
        routeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateFavoriteButton() {
        if (ride != null && btnFavorite != null) {
            if (ride.isFavourite()) {
                // Already a favorite - show FULL star, allow removal
                btnFavorite.setText(R.string.remove_from_favorites);
                btnFavorite.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.star_big_on, 0, 0, 0);
            } else {
                // Not a favorite - show EMPTY star, allow adding
                btnFavorite.setText(R.string.mark_as_favorite);
                btnFavorite.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.star_big_off, 0, 0, 0);
            }
        }
    }
}