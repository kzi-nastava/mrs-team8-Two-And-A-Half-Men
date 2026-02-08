package com.project.mobile.fragments.Unregistered;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.mobile.DTO.DriverLocationDto;
import com.project.mobile.DTO.Map.MarkerPointIcon;
import com.project.mobile.DTO.Map.NominatimResult;
import com.project.mobile.DTO.Ride.RideBookingParametersDTO;
import com.project.mobile.DTO.Ride.RouteItemDTO;
import com.project.mobile.R;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.map.MapFragment;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;
import com.project.mobile.map.mapForm.FormStops;
import com.project.mobile.map.mapForm.SearchFragment;
import com.project.mobile.viewModels.RideModel;

import java.util.List;


public class HomeUnregistered extends Fragment {

    private Button estimateButton;
    private Button restartButton;
    private TextView estimateTime;
    private RideModel rideModel;
    private MessageCallback callback;

    private MarkerDrawer markerDrawer;
    private SheredLocationViewModel sheredLocationViewModel;
    public HomeUnregistered() {
    }

    public static HomeUnregistered newInstance() {
        HomeUnregistered fragment = new HomeUnregistered();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sheredLocationViewModel =
                new ViewModelProvider(requireActivity()).get(SheredLocationViewModel.class);
            rideModel = new ViewModelProvider(requireActivity()).get(RideModel.class);
        markerDrawer = new ViewModelProvider(requireActivity()).get(MarkerDrawer.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_unregistered, container, false);
        callback = WebSocketManager.subscribe("/topic/driver-locations", locationUpdate -> {
            FragmentActivity activity = getActivity();
            if (activity != null && !activity.isFinishing() && isAdded()) {
                activity.runOnUiThread(() -> {
                    Log.d("FormStopsMap", "Received location update: " + locationUpdate);
                    DriverLocationDto driverLocation = DriverLocationDto.fromJson(locationUpdate);
                    if (driverLocation != null) {
                        Drawable carIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car);
                        markerDrawer.addMarker(new MarkerPointIcon(driverLocation.getLatitude(), driverLocation.getLongitude(), driverLocation.getDriverEmail(), carIcon));
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(() -> {
            Log.d("HomeUnregistered", "Fragment view size: " + view.getWidth() + "x" + view.getHeight());
            FrameLayout mapContainer = view.findViewById(R.id.map_container);
            Log.d("HomeUnregistered", "Map container size: " + mapContainer.getWidth() + "x" + mapContainer.getHeight());
        });
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_container , new SearchFragment())
                .replace(R.id.map_container, new MapFragment())
                .replace(R.id.info_container , new FormStops(2L))
                .commit();
        restartButton = view.findViewById(R.id.restart_button);
        restartButton.setOnClickListener(v -> {
            sheredLocationViewModel.clearStops();
        });
        estimateTime = view.findViewById(R.id.estimate_time);
        estimateButton = view.findViewById(R.id.estimate_button);
        estimateButton.setOnClickListener(v -> {
            List<NominatimResult> stops = sheredLocationViewModel.getStops().getValue();
                if (stops != null && stops.size() == 2) {
                    NominatimResult from = stops.get(0);
                    NominatimResult to = stops.get(1);
                    RideBookingParametersDTO rideData = new RideBookingParametersDTO();
                    RouteItemDTO fromRouteItem = new RouteItemDTO(from);
                    RouteItemDTO toRouteItem = new RouteItemDTO(to);
                    List<RouteItemDTO> routeItems = List.of(fromRouteItem, toRouteItem);
                    rideData.setRoute(routeItems);
                    rideModel.estimateRide(rideData).thenAccept(costTimeDTO -> {
                        if (costTimeDTO != null) {
                            String estimateText = "Estimated Time: " + costTimeDTO.getTime() + " mins";
                            estimateTime.setText(estimateText);
                        } else {
                            estimateTime.setText("Failed to get estimate");
                        }
                    });
                }
        });
    }
}