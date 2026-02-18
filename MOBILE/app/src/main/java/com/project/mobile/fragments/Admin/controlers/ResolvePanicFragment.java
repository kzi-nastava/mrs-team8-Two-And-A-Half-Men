package com.project.mobile.fragments.Admin.controlers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;

public class ResolvePanicFragment extends Fragment {

    private static final String ARG_RIDE_ID = "rideId";

    private Long rideId;
    private Button btnResolvePanic;
    private RideModel rideModel;

    public static ResolvePanicFragment newInstance(Long rideId) {
        ResolvePanicFragment fragment = new ResolvePanicFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rideModel = new ViewModelProvider(requireActivity()).get(RideModel.class);

        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resolve_panic, container, false);

        btnResolvePanic = view.findViewById(R.id.btn_resolve_panic);

        btnResolvePanic.setOnClickListener(v -> {
            btnResolvePanic.setEnabled(false); // prevent double click
            rideModel.resolvePanic(rideId);
        });

        observeViewModel();

        return view;
    }

    private void observeViewModel() {

        rideModel.getResolvePanicSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success == null) return;

            btnResolvePanic.setEnabled(true);

            if (success) {
                Toast.makeText(getContext(),
                        "Panic resolved successfully!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Failed to resolve panic.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        rideModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                btnResolvePanic.setEnabled(true);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
