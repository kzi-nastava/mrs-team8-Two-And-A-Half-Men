package com.project.mobile.fragments.Driver.controlers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;

public class AceptedRide extends Fragment {

    private Long rideId;
    private Button btnStartRide;
    private Button btnCancelRide;
    private RideModel rideModel;

    public static AceptedRide newInstance(Long rideId) {
        AceptedRide fragment = new AceptedRide();
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

        View view = inflater.inflate(R.layout.fragment_acepted_ride, container, false);

        btnStartRide = view.findViewById(R.id.start_ride_btn);
        btnCancelRide = view.findViewById(R.id.btn_cancel_driver);
        btnStartRide.setOnClickListener(v -> {
            rideModel.startRide(rideId);
        });
        btnCancelRide.setOnClickListener(v -> {
            Log.d("AceptedRide", "Cancel button clicked for ride ID: " + rideId);
            CancelRideDialogFragment dialog = new CancelRideDialogFragment(rideId);
            dialog.show(getParentFragmentManager(), "CancelRideDialog");
        });

        rideModel.getStartSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success == null) return;

            if (success) {
                Toast.makeText(getContext(),
                        "Ride started successfully!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Failed to start ride.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        rideModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}

