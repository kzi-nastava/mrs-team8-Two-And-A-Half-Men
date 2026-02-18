package com.project.mobile.map.mapForm;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.mobile.DTO.Map.NominatimResult;
import com.project.mobile.R;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.RouteDrawer;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;

import java.util.ArrayList;
import java.util.List;

public class FormStops extends Fragment {

    public MarkerDrawer markerDrawer;
    public RouteDrawer routeDrawer;
    public SheredLocationViewModel sheredLocationViewModel;

    private LinearLayout stopsContainer;

    public MessageCallback callback;

    private Long maxStops = null;
    private boolean isEditable = true;

    public FormStops() {
    }

    public FormStops(Long maxStops) {
        this.maxStops = maxStops;
    }

    public FormStops(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public FormStops(Long maxStops, boolean isEditable) {
        this.maxStops = maxStops;
        this.isEditable = isEditable;
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



        View view = inflater.inflate(R.layout.fragment_form_stops_map, container, false);

        stopsContainer = view.findViewById(R.id.stops_container);
        setupObservers();

        return view;
    }

    private void setupObservers() {
        // Observe stops - rebuild stop list when changed
        sheredLocationViewModel.getStops().observe(getViewLifecycleOwner(), stops -> {
            if(maxStops != null && stops != null && stops.size() > maxStops) {
                sheredLocationViewModel.RemoveLocation(1);
                return;
            }
            Log.d("FormStops", "Stops updated, count: " + (stops != null ? stops.size() : "null"));
            updateStopsList(stops);
        });
    }

    private void updateStopsList(java.util.List<NominatimResult> stops) {
        if (stopsContainer == null) return;

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
            Log.d("FormStops", "Adding stop to list: " + stop.display_name);
            View stopRow = createStopRow(stop, i, stops.size());
            if (stopRow != null) {
                stopsContainer.addView(stopRow);
            }
        }
    }

    private View createStopRow(NominatimResult stop, int index, int totalStops) {
        try {
            View row = LayoutInflater.from(getContext())
                    .inflate(R.layout.row_stops, stopsContainer, false);

            TextView txtLabel = row.findViewById(R.id.txt_stop_label);
            TextView txtAddress = row.findViewById(R.id.txt_stop_address);
            ImageButton btnDelete = row.findViewById(R.id.btn_delete_stop);
            ImageButton btnMoveUp = row.findViewById(R.id.btn_move_up);
            ImageButton btnMoveDown = row.findViewById(R.id.btn_move_down);

            if (txtLabel != null) {
                txtLabel.setText(String.valueOf((char)('A' + index)));
            }
            if (txtAddress != null) {
                txtAddress.setText(stop.display_name);
            }

            // Control delete button
            if (btnDelete != null) {
                if(isEditable) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(v -> {
                        sheredLocationViewModel.RemoveLocation(index);
                    });
                } else {
                    btnDelete.setVisibility(View.GONE);
                }
            }

            // Control move up button
            if (btnMoveUp != null) {
                if (isEditable && index > 0) {
                    btnMoveUp.setVisibility(View.VISIBLE);
                    btnMoveUp.setOnClickListener(v -> {
                        moveStop(index, index - 1);
                    });
                } else {
                    btnMoveUp.setVisibility(View.GONE);
                }
            }

            // Control move down button
            if (btnMoveDown != null) {
                if (isEditable && index < totalStops - 1) {
                    btnMoveDown.setVisibility(View.VISIBLE);
                    btnMoveDown.setOnClickListener(v -> {
                        moveStop(index, index + 1);
                    });
                } else {
                    btnMoveDown.setVisibility(View.GONE);
                }
            }

            return row;
        } catch (Exception e) {
            Log.e("FormStops", "Error creating stop row: " + e.getMessage(), e);
            return null;
        }
    }

    private void moveStop(int fromIndex, int toIndex) {
        try {
            List<NominatimResult> stops = sheredLocationViewModel.getStops().getValue();
            if (stops == null || fromIndex < 0 || toIndex < 0 ||
                    fromIndex >= stops.size() || toIndex >= stops.size()) {
                return;
            }

            // Create a new list with moved item
            List<NominatimResult> newStops = new ArrayList<>(stops);
            NominatimResult item = newStops.remove(fromIndex);
            newStops.add(toIndex, item);

            // Update the ViewModel
            sheredLocationViewModel.updateStopsOrder(newStops);
        } catch (Exception e) {
            Log.e("FormStops", "Error moving stop: " + e.getMessage(), e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callback != null) {
            WebSocketManager.unsubscribe("/topic/driver-locations", callback);
        }
    }
}