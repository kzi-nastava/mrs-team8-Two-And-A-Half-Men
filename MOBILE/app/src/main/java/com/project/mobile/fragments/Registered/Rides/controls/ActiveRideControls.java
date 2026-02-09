package com.project.mobile.fragments.Registered.Rides.controls;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;


public class ActiveRideControls extends Fragment {

    private Long DriveID;
    private String accessToken;

    private Button btnLeaveNote;
    private Button panicButton;

    private TextView txtNote;

    private RideModel rideModel;

    public ActiveRideControls() {
    }
    public static ActiveRideControls newInstance(Long driveID, String accessToken) {
        ActiveRideControls fragment = new ActiveRideControls();
        Bundle args = new Bundle();
        args.putLong("DriveID", driveID);
        args.putString("accessToken", accessToken);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideModel = new ViewModelProvider(requireActivity()).get(RideModel.class);
        if (getArguments() != null) {
            DriveID = getArguments().getLong("DriveID");
            accessToken = getArguments().getString("accessToken");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_active_ride_controls, container, false);
        btnLeaveNote = view.findViewById(R.id.btn_save_note);
        panicButton = view.findViewById(R.id.btnPanic);
        txtNote = view.findViewById(R.id.etNote);

        btnLeaveNote.setOnClickListener(v -> {
            String noteContent = txtNote.getText().toString();
            if (!noteContent.isEmpty()) {
                rideModel.addRideNote(DriveID, accessToken, noteContent);
            } else {
                Toast.makeText(getContext(), "Please enter a note before submitting.", Toast.LENGTH_SHORT).show();
            }
        });
        panicButton.setOnClickListener(v -> {
            rideModel.triggerPanic(accessToken);
        });


        rideModel.getNoteResponse().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), "Note sent!", Toast.LENGTH_SHORT).show();
            }
        });
        rideModel.getPanicSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Panic alert triggered successfully!", Toast.LENGTH_SHORT).show();
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