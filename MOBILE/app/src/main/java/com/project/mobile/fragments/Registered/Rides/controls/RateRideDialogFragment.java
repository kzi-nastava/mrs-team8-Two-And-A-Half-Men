package com.project.mobile.fragments.Registered.Rides.controls;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.project.mobile.DTO.Ride.RideDTO;
import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;

public class RateRideDialogFragment extends DialogFragment {

    private static final String TAG = "RateRideDialog";
    private static final String ARG_RIDE = "ride";
    private static final String ARG_ACCESS_TOKEN = "accessToken";

    private RideDTO ride;
    private String accessToken;
    private RideModel rideModel;

    // UI Components
    private ImageButton btnClose;
    private ImageView[] driverStars = new ImageView[5];
    private ImageView[] vehicleStars = new ImageView[5];
    private EditText etComment;
    private Button btnConfirm;

    // Rating values
    private int driverRating = 0;
    private int vehicleRating = 0;

    public static RateRideDialogFragment newInstance(RideDTO ride, String accessToken) {
        RateRideDialogFragment fragment = new RateRideDialogFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(ARG_RIDE, gson.toJson(ride));
        args.putString(ARG_ACCESS_TOKEN, accessToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideModel = new ViewModelProvider(requireActivity()).get(RideModel.class);

        if (getArguments() != null) {
            Gson gson = new Gson();
            String rideJson = getArguments().getString(ARG_RIDE);
            ride = gson.fromJson(rideJson, RideDTO.class);
            accessToken = getArguments().getString(ARG_ACCESS_TOKEN);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_ride_dialog, container, false);

        // Initialize views
        btnClose = view.findViewById(R.id.btn_close);
        etComment = view.findViewById(R.id.et_comment);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        // Initialize driver stars
        driverStars[0] = view.findViewById(R.id.driver_star_1);
        driverStars[1] = view.findViewById(R.id.driver_star_2);
        driverStars[2] = view.findViewById(R.id.driver_star_3);
        driverStars[3] = view.findViewById(R.id.driver_star_4);
        driverStars[4] = view.findViewById(R.id.driver_star_5);

        // Initialize vehicle stars
        vehicleStars[0] = view.findViewById(R.id.vehicle_star_1);
        vehicleStars[1] = view.findViewById(R.id.vehicle_star_2);
        vehicleStars[2] = view.findViewById(R.id.vehicle_star_3);
        vehicleStars[3] = view.findViewById(R.id.vehicle_star_4);
        vehicleStars[4] = view.findViewById(R.id.vehicle_star_5);

        setupStarListeners();
        setupButtons();
        observeViewModel();

        return view;
    }

    private void setupStarListeners() {
        // Driver stars
        for (int i = 0; i < driverStars.length; i++) {
            final int rating = i + 1;
            driverStars[i].setOnClickListener(v -> {
                driverRating = rating;
                updateDriverStars();
            });
        }

        // Vehicle stars
        for (int i = 0; i < vehicleStars.length; i++) {
            final int rating = i + 1;
            vehicleStars[i].setOnClickListener(v -> {
                vehicleRating = rating;
                updateVehicleStars();
            });
        }
    }

    private void updateDriverStars() {
        for (int i = 0; i < driverStars.length; i++) {
            if (i < driverRating) {
                driverStars[i].setImageResource(android.R.drawable.star_big_on);
            } else {
                driverStars[i].setImageResource(android.R.drawable.star_big_off);
            }
        }
    }

    private void updateVehicleStars() {
        for (int i = 0; i < vehicleStars.length; i++) {
            if (i < vehicleRating) {
                vehicleStars[i].setImageResource(android.R.drawable.star_big_on);
            } else {
                vehicleStars[i].setImageResource(android.R.drawable.star_big_off);
            }
        }
    }

    private void setupButtons() {
        btnClose.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        // Validate ratings
        if (driverRating == 0 || vehicleRating == 0) {
            Toast.makeText(getContext(), "Please rate both driver and vehicle", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ride == null || ride.getId() == null) {
            Toast.makeText(getContext(), "Invalid ride data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get comment (can be empty)
        String comment = etComment.getText().toString().trim();
        if (comment.isEmpty()) {
            comment = null; // Send null if no comment
        }

        // Disable button to prevent double submission
        btnConfirm.setEnabled(false);

        Log.d(TAG, "Submitting rating - Driver: " + driverRating + ", Vehicle: " + vehicleRating);

        // Call the ViewModel method
        rideModel.rateRide(ride.getId(), accessToken, driverRating, vehicleRating, comment);
    }

    private void observeViewModel() {
        // Observe rating success
        rideModel.getRatingSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                btnConfirm.setEnabled(true);

                if (success) {
                    Toast.makeText(getContext(), "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe errors
        rideModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                btnConfirm.setEnabled(true);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        rideModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                btnConfirm.setEnabled(!isLoading);
                btnConfirm.setText(isLoading ? "Submitting..." : "Confirm");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make dialog full width
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}