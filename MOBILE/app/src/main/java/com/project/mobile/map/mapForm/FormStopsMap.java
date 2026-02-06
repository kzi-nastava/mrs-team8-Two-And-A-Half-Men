package com.project.mobile.map.mapForm;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.mobile.DTO.DriverLocationDto;
import com.project.mobile.DTO.MarkerPointIcon;
import com.project.mobile.DTO.Route;
import com.project.mobile.R;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.RouteDrawer;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormStopsMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormStopsMap extends Fragment {

    public MarkerDrawer markerDrawer;
    public RouteDrawer routeDrawer;
    public SheredLocationViewModel sheredLocationViewModel;

    public MessageCallback callback;

    public FormStopsMap() {
    }


    public static FormStopsMap newInstance() {
        FormStopsMap fragment = new FormStopsMap();
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
        return inflater.inflate(R.layout.fragment_form_stops_map, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketManager.unsubscribe("/topic/driver-locations", callback);
    }
}