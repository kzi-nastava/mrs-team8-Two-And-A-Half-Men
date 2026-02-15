package com.project.mobile.fragments.Driver.controlers;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.DTO.Ride.CostTimeDTO;
import com.project.mobile.DTO.Ride.FinishRideDTO;
import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;

public class ActiveRideDriver extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private RideModel viewModel;
    private Long rideId;

    public static ActiveRideDriver newInstance(Long rideId) {
        ActiveRideDriver fragment = new ActiveRideDriver();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_ride_driver, container, false);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(RideModel.class);

        Button btnPanic = view.findViewById(R.id.btn_panic_driver);
        Button btnEndRide = view.findViewById(R.id.btn_end_ride_driver);

        btnPanic.setOnClickListener(v -> viewModel.triggerPanic(null));

        btnEndRide.setOnClickListener(v -> showConfirmEndDialog());

        setupObservers();

        return view;
    }

    private void setupObservers() {
        viewModel.getEndRideResult().observe(getViewLifecycleOwner(), costTimeDTO -> {
            if (costTimeDTO != null) {
                showSummaryDialog(costTimeDTO);
            }
        });

        viewModel.getFinishSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Ride Finished Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getPanicSuccess().observe(getViewLifecycleOwner(), success -> {;
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Panic Triggered Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmEndDialog() {
        View v = getLayoutInflater().inflate(R.layout.are_you_sure, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();

        v.findViewById(R.id.btn_yes_end).setOnClickListener(view -> {
            viewModel.endRide(rideId);
            dialog.dismiss();
        });
        TextView tvMessage = v.findViewById(R.id.areYouSureText);
        tvMessage.setText("Are you sure you want to end the ride?");
        TextView tvTitle = v.findViewById(R.id.areYouSureTitle);
        tvTitle.setText("End Ride Confirmation");
        v.findViewById(R.id.btn_no_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void showSummaryDialog(CostTimeDTO data) {
        View v = getLayoutInflater().inflate(R.layout.dialog_ride_summery, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();

        TextView tvCost = v.findViewById(R.id.tv_cost_display);
        TextView tvTime = v.findViewById(R.id.tv_duration_display);
        CheckBox cbInterrupted = v.findViewById(R.id.cb_is_interrupted);

        // Display data as plain text
        tvCost.setText(String.format("%.2f RSD", data.getCost()));
        tvTime.setText(String.format("%d min", (int) data.getTime()));

        v.findViewById(R.id.btn_finish_ride).setOnClickListener(view -> {
            FinishRideDTO finishDTO = new FinishRideDTO(cbInterrupted.isChecked(), true);
            viewModel.finishRide(rideId, finishDTO);
            dialog.dismiss();
        });

        v.findViewById(R.id.btn_summary_cancel).setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }
}