package com.project.mobile.fragments.Registered.Rides;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mobile.DTO.DriverLocationDto;
import com.project.mobile.DTO.Map.MarkerPointIcon;
import com.project.mobile.DTO.Map.NominatimResult;
import com.project.mobile.DTO.Ride.RideTrackingDTO;
import com.project.mobile.DTO.Ride.RouteItemDTO;
import com.project.mobile.R;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.fragments.Registered.Rides.controls.ActiveRideControls;
import com.project.mobile.fragments.Registered.Rides.controls.PendingRideControls;
import com.project.mobile.map.MapFragment;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.RouteDrawer;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;
import com.project.mobile.map.mapForm.FormStops;
import com.project.mobile.viewModels.RideModel;

import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class RideDetailsFragment extends Fragment {


    private static final String TAG = "RideDetailsFragment";
    private static final String ARG_RIDE_ID = "ride_id";
    private static final String ARG_ACCESS_TOKEN = "access_token";

    private RideModel rideModel;
    private MarkerDrawer markerDrawer;
    private RouteDrawer routeDrawer;
    private SheredLocationViewModel sheredLocationViewModel;
    private ProgressBar progressBar;
    private TextView errorView;
    private View contentContainer;

    private TextView txtRideId;
    private TextView txtStatus;
    private TextView txtStartTime;
    private TextView txtTime;
    public Long DriverID;
    private TextView txtDistance;

    private Long rideId;
    private String accessToken;

    private FrameLayout actionFrame;

    private CardView actionCard;
    private MessageCallback callback;

    private int updateCounter = 0;

    public RideDetailsFragment() {
    }

    public static RideDetailsFragment newInstanceWithId(Long rideId) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    public static RideDetailsFragment newInstanceWithAccessToken(String accessToken) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESS_TOKEN, accessToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_RIDE_ID)) {
                rideId = getArguments().getLong(ARG_RIDE_ID);
            }
            if (getArguments().containsKey(ARG_ACCESS_TOKEN)) {
                accessToken = getArguments().getString(ARG_ACCESS_TOKEN);
            }
        }
        rideModel = new ViewModelProvider(this).get(RideModel.class);
        sheredLocationViewModel = new ViewModelProvider(requireActivity()).get(SheredLocationViewModel.class);
        markerDrawer = new ViewModelProvider(requireActivity()).get(MarkerDrawer.class);
        routeDrawer = new ViewModelProvider(requireActivity()).get(RouteDrawer.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_details, container, false);

        initializeViews(view);
        setupFragments();
        setupObservers();
        loadRideData();

        return view;
    }

    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        errorView = view.findViewById(R.id.error_view);
        contentContainer = view.findViewById(R.id.content_container);

        txtRideId = view.findViewById(R.id.txt_ride_id);
        txtStatus = view.findViewById(R.id.txt_ride_status);
        txtStartTime = view.findViewById(R.id.txt_start_time);
        txtTime = view.findViewById(R.id.txt_time);
        txtDistance = view.findViewById(R.id.txt_distance);
        actionFrame = view.findViewById(R.id.actions_panel);
        actionCard = view.findViewById(R.id.action_card);
    }

    private void setupFragments() {
        // Add MapFragment (non-clickable for viewing only)
        MapFragment mapFragment = new MapFragment(false);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        // Add FormStops (non-editable for viewing only)
        FormStops formStops = new FormStops(false);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.stops_container, formStops)
                .commit();
    }

    private void setupObservers() {
        rideModel.getRideTracking().observe(getViewLifecycleOwner(), rideTracking -> {
            if (rideTracking != null) {
                displayRideTracking(rideTracking);
                contentContainer.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            }
        });

        rideModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        rideModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                errorView.setText(error);
                errorView.setVisibility(View.VISIBLE);
                contentContainer.setVisibility(View.GONE);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRideData() {
        if (accessToken != null && !accessToken.isEmpty()) {
            // Unregistered user with access token
            Log.d(TAG, "Loading ride with access token");
            rideModel.loadRideByAccessToken(accessToken);
        } else if (rideId != null) {
            // Registered user with ride ID
            Log.d(TAG, "Loading ride with ID: " + rideId);
            rideModel.loadRideById(rideId);
        } else {
            errorView.setText("No ride information provided");
            errorView.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.GONE);
        }
    }

    private void displayRideTracking(RideTrackingDTO rideTracking) {
        Log.d(TAG, "Displaying ride tracking for ride ID: " + rideTracking.getId());

        setTextSafe(txtRideId, "Ride #" + rideTracking.getId());
        setTextSafe(txtStatus, formatStatus(rideTracking.getStatus()));
        setTextSafe(txtStartTime, formatDateTime(rideTracking.getStartTime()));

        String status = rideTracking.getStatus();
        if(status.equals("ACTIVE")) {
            actionCard.setVisibility(View.VISIBLE);
            actionFrame.setVisibility(View.VISIBLE);
            ActiveRideControls activeRideControls = ActiveRideControls.newInstance(rideTracking.getId(), accessToken);
            getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , activeRideControls).commit();
            txtDistance.setVisibility(View.VISIBLE);
            txtTime.setVisibility(View.VISIBLE);
            callback = WebSocketManager.subscribe("/topic/driver-locations/" + rideTracking.getDriverId(), locationUpdate -> {
                FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing() && isAdded()) {
                    activity.runOnUiThread(() -> {
                        Log.d("FormStopsMap", "Received location update: " + locationUpdate);
                        DriverLocationDto driverLocation = DriverLocationDto.fromJson(locationUpdate);
                        if (driverLocation != null) {
                            Drawable carIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car);
                            carIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                            markerDrawer.addMarker(new MarkerPointIcon(driverLocation.getLatitude(), driverLocation.getLongitude(), driverLocation.getDriverEmail(), carIcon));
                            if(updateCounter == 0) {
                                List<RouteItemDTO> stops = rideTracking.getStops();
                                List<GeoPoint> geoPoints = new ArrayList<>();
                                geoPoints.add(new GeoPoint(driverLocation.getLatitude(), driverLocation.getLongitude()));
                                for (RouteItemDTO stop : stops) {
                                    geoPoints.add(new GeoPoint(stop.getLatitude(), stop.getLongitude()));
                                }
                                routeDrawer.fetchRouteTimeAndDistanceAsync(geoPoints).thenAccept(result -> {
                                            double totalDuration = result.get(0);
                                            double totalDistance = result.get(1);
                                                String timeText = String.format(Locale.getDefault(), "Estimated Time: %.1f mins", totalDuration / 60);
                                                String distanceText = String.format(Locale.getDefault(), "Estimated Distance: %.1f km", totalDistance / 1000);
                                                txtTime.setText(timeText);
                                                txtDistance.setText(distanceText);
                                            Log.d("RouteInfo", "Total Duration: " + totalDuration + " sec");
                                            Log.d("RouteInfo", "Total Distance: " + totalDistance + " m");
                                        })
                                        .exceptionally(e -> {
                                            Log.e("RouteInfo", "Error fetching route info", e);
                                            return null;
                                        });
                                updateCounter=11;
                            }

                            updateCounter--;
                        }
                    });
                }
            });
            DriverID = rideTracking.getDriverId();
        } else if (status.equals("PENDING")) {
            Log.d("RideDetailsFragment", "Ride is pending. Access token: " + accessToken);
            if(accessToken == null && getMinutesUntilRide(rideTracking.getStartTime()) >= 15) {
                actionCard.setVisibility(View.VISIBLE);
                actionFrame.setVisibility(View.VISIBLE);
                PendingRideControls pendingRide = PendingRideControls.newInstance(rideTracking.getId());
                getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , pendingRide).commit();
            }
        }


        updateMapWithStops(rideTracking.getStops());
    }

    private void updateMapWithStops(List<RouteItemDTO> stops) {
        if (stops != null && !stops.isEmpty()) {
            sheredLocationViewModel.clearStops();
            for (RouteItemDTO stop : stops) {
                NominatimResult location = new NominatimResult(stop.getAddress(), stop.getLatitude(), stop.getLongitude());
                sheredLocationViewModel.addLocation(location);
            }

        }
    }
    private String formatStatus(String status) {
        if (status == null) return "Unknown";

        switch (status.toUpperCase()) {
            case "PENDING":
                return "Pending";
            case "ACCEPTED":
                return "Accepted";
            case "IN_PROGRESS":
                return "In Progress";
            case "COMPLETED":
                return "Completed";
            case "CANCELLED":
                return "Cancelled";
            default:
                return status;
        }
    }

    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return "-";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

            Date date = inputFormat.parse(dateTimeStr);
            return date != null ? outputFormat.format(date) : dateTimeStr;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateTimeStr, e);
            return dateTimeStr;
        }
    }

    private void setTextSafe(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text != null ? text : "-");
        }
    }
    @Override
    public void onDestroyView() {
        if (callback != null) {
            WebSocketManager.unsubscribe("/topic/driver-locations/" + DriverID , callback);
        }
        super.onDestroyView();
        if (sheredLocationViewModel != null) {
            sheredLocationViewModel.clearStops();
        }

    }
    public static long getMinutesUntilRide(String rideTimeString) {
        if (rideTimeString == null || rideTimeString.isEmpty()) {
            return -1;
        }

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String cleanedTime = rideTimeString.replace("Z", "").split("\\+")[0].split("\\.")[0];
            Date rideTime = isoFormat.parse(cleanedTime);

            if (rideTime == null) {
                return -1;
            }

            long diffMillis = rideTime.getTime() - System.currentTimeMillis();
            return TimeUnit.MILLISECONDS.toMinutes(diffMillis);

        } catch (ParseException e) {
            Log.e("RideTimeUtils", "Failed to parse ride time", e);
            return -1;
        }
    }
}