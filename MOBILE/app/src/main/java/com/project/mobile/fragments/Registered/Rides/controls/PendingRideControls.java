package com.project.mobile.fragments.Registered.Rides.controls;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;

public class PendingRideControls extends Fragment {

    private Long rideId;

    private Button btnCancelRide;

    private RideModel rideModel;

    public PendingRideControls() {
    }

    public static PendingRideControls newInstance(Long rideId) {
        PendingRideControls fragment = new PendingRideControls();
        Bundle args = new Bundle();
        args.putLong("rideId", rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideModel = new ViewModelProvider(requireActivity()).get(RideModel.class);
        if (getArguments() != null) {
            rideId = getArguments().getLong("rideId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_ride_controls, container, false);

        btnCancelRide = view.findViewById(R.id.btnCancelRide);

        btnCancelRide.setOnClickListener(v -> {
            showCancelConfirmationDialog();
        });

        // Observe cancel success
        rideModel.getCancelSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Ride cancelled successfully!", Toast.LENGTH_SHORT).show();
                // Optionally navigate back or refresh the list
            } else if (success != null && !success) {
                Toast.makeText(getContext(), "Failed to cancel ride", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe errors
        rideModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Ride")
                .setMessage("Are you sure you want to cancel this ride?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Call the cancel method with null values as per requirement
                    rideModel.cancelRide(rideId, null, null);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}
