package com.project.mobile.fragments.Driver.controlers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;


public class CancelRideDialogFragment extends DialogFragment {

    private Long rideId;


    public CancelRideDialogFragment(Long rideId ) {
        Log.d("CancelRideDialog", "Creating dialog for ride ID: " + rideId);
        this.rideId = rideId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancel_ride_dialog, container, false);
        RideModel viewModel = new ViewModelProvider(requireActivity()).get(RideModel.class);
        Spinner spinner = view.findViewById(R.id.spinner_cancelled_by);
        EditText etReason = view.findViewById(R.id.et_cancel_reason);
        Button btnConfirm = view.findViewById(R.id.btn_confirm_cancel);
        Button btnDismiss = view.findViewById(R.id.btn_dismiss_dialog);

        btnDismiss.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            String cancelledBy = spinner.getSelectedItem().toString();

            if (cancelledBy.equals("Select...")) {
                Toast.makeText(getContext(), "Please select who is cancelling", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("CancelRideDialog", "Cancelling ride ID: " + rideId + " by: " + cancelledBy + " with reason: " + reason);
            // Call your ViewModel method
            viewModel.cancelRide(rideId, reason, cancelledBy);
            dismiss();
        });

        return view;
    }
}