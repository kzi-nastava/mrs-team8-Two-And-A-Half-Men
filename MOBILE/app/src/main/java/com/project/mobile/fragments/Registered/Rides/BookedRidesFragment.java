package com.project.mobile.fragments.Registered.Rides;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mobile.R;
import com.project.mobile.viewModels.RideModel;


public class BookedRidesFragment extends Fragment {
    private RideModel rideModel;
    private RecyclerView recyclerView;
    private BookedRidesAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    public BookedRidesFragment() {
    }

    public static BookedRidesFragment newInstance() {
        return new BookedRidesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideModel = new ViewModelProvider(this).get(RideModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booked_rides, container, false);

        recyclerView = view.findViewById(R.id.recycler_booked_rides);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);

        setupRecyclerView();
        setupObservers();

        rideModel.loadBookedRides();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookedRidesAdapter(ride -> {
            openRideDetails(ride.getId());
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        rideModel.getBookedRides().observe(getViewLifecycleOwner(), rides -> {
            if (rides != null && !rides.isEmpty()) {
                adapter.setRides(rides);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });

        rideModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        rideModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openRideDetails(Long rideId) {
        Log.d("BookedRidesFragment", "Opening ride details for ID: " + rideId);

        RideDetailsFragmentActive rideDetailsFragment = RideDetailsFragmentActive.newInstanceWithId(rideId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, rideDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

}