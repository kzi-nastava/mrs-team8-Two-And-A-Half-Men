package com.project.mobile.map.mapForm;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.mobile.DTO.DriverLocationDto;
import com.project.mobile.DTO.MarkerPointIcon;
import com.project.mobile.DTO.NominatimResult;
import com.project.mobile.R;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.RouteDrawer;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;

public class FormStops extends Fragment {

    public MarkerDrawer markerDrawer;
    public RouteDrawer routeDrawer;
    public SheredLocationViewModel sheredLocationViewModel;

    private LinearLayout stopsContainer;

    public MessageCallback callback;

    private Long maxStops = null;

    public FormStops() {
    }
    public FormStops(Long maxStops) {
        this.maxStops = maxStops;
    }

    public static FormStops newInstance() {
        FormStops fragment = new FormStops();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.markerDrawer = new ViewModelProvider(requireActivity()).get(MarkerDrawer.class);
        this.routeDrawer = new ViewModelProvider(requireActivity()).get(RouteDrawer.class);
        this.sheredLocationViewModel = new ViewModelProvider(requireActivity()).get(SheredLocationViewModel.class);
        routeDrawer.startDrawingRoute(this, this.sheredLocationViewModel.getStops(), 0xFF0000FF, "10.0f");
        callback = WebSocketManager.subscribe("/topic/driver-locations", locationUpdate -> {
            FragmentActivity activity = getActivity();
            if (activity != null && !activity.isFinishing() && isAdded()) {
                activity.runOnUiThread(() -> {
                    Log.d("FormStopsMap", "Received location update: " + locationUpdate);
                    DriverLocationDto driverLocation = DriverLocationDto.fromJson(locationUpdate);
                    if (driverLocation != null) {
                        Drawable carIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car);
                        markerDrawer.addMarker(new MarkerPointIcon( driverLocation.getLatitude(), driverLocation.getLongitude(), driverLocation.getDriverEmail(), carIcon));
                    }
                });
            }
        });


        View view = inflater.inflate(R.layout.fragment_form_stops_map, container, false);

        stopsContainer = view.findViewById(R.id.stops_container);
        setupObservers();
        return view;
    }

    private void setupObservers() {

        // Observe stops - rebuild stop list when changed
        sheredLocationViewModel.getStops().observe(getViewLifecycleOwner(), stops -> {
            if(maxStops != null && stops.size() > maxStops) {
                sheredLocationViewModel.RemoveLocation(1);
                return;
            }
            updateStopsList(stops);
        });

    }
    private void updateStopsList(java.util.List<NominatimResult> stops) {
        stopsContainer.removeAllViews();

        if (stops == null || stops.isEmpty()) {
            TextView emptyText = new TextView(getContext());
            emptyText.setText("No stops added yet.\nSearch and select locations above.");
            emptyText.setTextColor(0xFF999999);
            emptyText.setTextSize(14);
            emptyText.setPadding(16, 32, 16, 32);
            emptyText.setGravity(android.view.Gravity.CENTER);
            stopsContainer.addView(emptyText);
            return;
        }
        for (int i = 0; i < stops.size(); i++) {
            NominatimResult stop = stops.get(i);
            View stopRow = createStopRow(stop, i);
            stopsContainer.addView(stopRow);
        }
    }
    private View createStopRow(NominatimResult stop, int index) {
        View row = LayoutInflater.from(getContext())
                .inflate(R.layout.row_stops, stopsContainer, false);

        TextView txtLabel = row.findViewById(R.id.txt_stop_label);
        TextView txtAddress = row.findViewById(R.id.txt_stop_address);
        ImageButton btnDelete = row.findViewById(R.id.btn_delete_stop);
        txtLabel.setText(String.valueOf((char)('A' + index)));
        txtAddress.setText(stop.display_name);
        btnDelete.setOnClickListener(v -> {
            sheredLocationViewModel.RemoveLocation(index);
        });
        return row;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketManager.unsubscribe("/topic/driver-locations", callback);
    }
}