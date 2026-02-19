package com.project.mobile.fragments.Admin.panic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.FragmentTransition;
import com.project.mobile.R;
import com.project.mobile.RideHistoryAdapter;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.fragments.RideDetailsFragmentActive;
import com.project.mobile.models.Ride;
import com.project.mobile.service.RideService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicHandleFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateText;

    private RideHistoryAdapter adapter;
    private List<Ride> rideList;
    private MessageCallback webSocketCallback;
    private final RideService rideService =
            RetrofitClient.retrofit.create(RideService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_panic_handle, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        setupRecyclerView();
        loadPanicRides();
        webSocketCallback = WebSocketManager.subscribe("/topic/panic" , message -> {
            getActivity().runOnUiThread(this::loadPanicRides);
        });

         view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // No action needed
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                WebSocketManager.unsubscribe("/topic/panic", webSocketCallback);
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        rideList = new ArrayList<>();
        adapter = new RideHistoryAdapter(rideList, this::onRideClick);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadPanicRides() {

        showLoading(true);

        Call<List<Ride>> call = rideService.getPanicingRides();

        call.enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ride>> call,
                                   @NonNull Response<List<Ride>> response) {

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {

                    rideList.clear();
                    rideList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (rideList.isEmpty()) {
                        emptyStateText.setVisibility(View.VISIBLE);
                    } else {
                        emptyStateText.setVisibility(View.GONE);
                    }

                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Ride>> call,
                                  @NonNull Throwable t) {

                showLoading(false);
                Log.e("PanicHandleFragment", "Network error", t);

                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onRideClick(Ride ride) {

        RideDetailsFragmentActive rideDetailsFragment = RideDetailsFragmentActive.newInstanceWithId(ride.getId());
        FragmentTransition.to(rideDetailsFragment, this.getActivity(), true, R.id.fragment_container_view_tag, "DELETE");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void handleError(int code) {

        String errorMsg = "Failed to load panic rides";

        if (code == 401) {
            errorMsg = "Unauthorized";
        } else if (code == 403) {
            errorMsg = "Access forbidden (ADMIN only)";
        } else if (code >= 500) {
            errorMsg = "Server error";
        }

        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
