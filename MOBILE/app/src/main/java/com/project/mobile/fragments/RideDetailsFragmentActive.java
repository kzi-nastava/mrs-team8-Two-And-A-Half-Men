    package com.project.mobile.fragments;

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
    import android.view.MotionEvent;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.FrameLayout;
    import android.widget.LinearLayout;
    import android.widget.ProgressBar;
    import android.widget.ScrollView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.project.mobile.DTO.Auth.MeInfo;
    import com.project.mobile.DTO.DriverLocationDto;
    import com.project.mobile.DTO.Map.MarkerPointIcon;
    import com.project.mobile.DTO.Map.NominatimResult;
    import com.project.mobile.DTO.Ride.PassengerDTO;
    import com.project.mobile.DTO.Ride.RideDTO;
    import com.project.mobile.DTO.Ride.RouteItemDTO;
    import com.project.mobile.R;
    import com.project.mobile.core.WebSocketsMenager.MessageCallback;
    import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
    import com.project.mobile.fragments.Driver.controlers.AceptedRide;
    import com.project.mobile.fragments.Driver.controlers.ActiveRideDriver;
    import com.project.mobile.fragments.Registered.Rides.controls.CancelledRideControls;
    import com.project.mobile.fragments.Registered.Rides.controls.PendingRideControls;
    import com.project.mobile.map.MapFragment;
    import com.project.mobile.map.ViewModel.MarkerDrawer;
    import com.project.mobile.map.ViewModel.RouteDrawer;
    import com.project.mobile.map.ViewModel.SheredLocationViewModel;
    import com.project.mobile.map.mapForm.FormStops;
    import com.project.mobile.viewModels.AuthModel;
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

    public class RideDetailsFragmentActive extends Fragment {


        private static final String TAG = "RideDetailsFragment";
        private static final String ARG_RIDE_ID = "ride_id";
        private static final String ARG_ACCESS_TOKEN = "access_token";

        private RideModel rideModel;
        private AuthModel authModel;
        private MarkerDrawer markerDrawer;
        private RouteDrawer routeDrawer;
        private SheredLocationViewModel sheredLocationViewModel;
        private ProgressBar progressBar;
        private TextView errorView;
        private View contentContainer;

        private TextView txtRideId;
        private TextView txtStatus;
        private TextView txtScheduledTime;
        private TextView txtStartTime;
        private TextView txtEndTime;
        private TextView txtPrice;
        private TextView txtTotalCost;
        private TextView txtEstimatedTime;
        private TextView txtEstimatedDistance;
        private TextView txtDriverName;
        private TextView txtRideOwnerName;
        private TextView txtPassengers;
        private TextView txtServices;
        private TextView txtCancellationReason;
        private TextView txtPath;
        private ScrollView scrollView;

        private LinearLayout layoutScheduledTime;
        private LinearLayout layoutStartTime;
        private LinearLayout layoutEndTime;
        private LinearLayout layoutPrice;
        private LinearLayout layoutTotalCost;
        private LinearLayout layoutEstimatedTime;
        private LinearLayout layoutEstimatedDistance;
        private LinearLayout layoutDriver;
        private LinearLayout layoutRideOwner;
        private LinearLayout layoutPassengers;
        private LinearLayout layoutPath;
        private CardView cardReviews;
        private LinearLayout reviewsContainer;




        private CardView cardPeople;
        private CardView cardServices;
        private CardView cardCancellation;


        private Long rideId;
        private String accessToken;
        private FrameLayout actionFrame;

        private CardView actionCard;
        private MessageCallback callback;
        public Long DriverID;

        private int updateCounter = 0;
        private MeInfo currentUser;
        private float lastScrollY = 0;
        private boolean isRefreshing = false;
        private static final int SCROLL_THRESHOLD = 50; // Threshold in pixels for triggering refresh


        public RideDetailsFragmentActive() {
        }

        public static RideDetailsFragmentActive newInstanceWithId(Long rideId) {
            RideDetailsFragmentActive fragment = new RideDetailsFragmentActive();
            Bundle args = new Bundle();
            args.putLong(ARG_RIDE_ID, rideId);
            fragment.setArguments(args);
            return fragment;
        }

        public static RideDetailsFragmentActive newInstanceWithAccessToken(String accessToken, Long rideId) {
            RideDetailsFragmentActive fragment = new RideDetailsFragmentActive();
            Bundle args = new Bundle();
            args.putString(ARG_ACCESS_TOKEN, accessToken);
            args.putLong(ARG_RIDE_ID, rideId);
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
            authModel = new ViewModelProvider(this).get(AuthModel.class);
            loadUserInfo();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.ride_details_fragment_active, container, false);

            initializeViews(view);
            setupFragments();
            setupObservers();
            setupScrollToRefresh();
            loadRideData();

            return view;
        }

        private void initializeViews(View view) {
            progressBar = view.findViewById(R.id.progress_bar);
            errorView = view.findViewById(R.id.error_view);
            contentContainer = view.findViewById(R.id.content_container);
            scrollView = (ScrollView) view.findViewById(R.id.content_container);
            txtRideId = view.findViewById(R.id.txt_ride_id);
            txtStatus = view.findViewById(R.id.txt_ride_status);
            txtScheduledTime = view.findViewById(R.id.txt_scheduled_time);
            txtStartTime = view.findViewById(R.id.txt_start_time);
            txtEndTime = view.findViewById(R.id.txt_end_time);
            txtPrice = view.findViewById(R.id.txt_price);
            txtTotalCost = view.findViewById(R.id.txt_total_cost);
            txtEstimatedTime = view.findViewById(R.id.txt_estimated_time);
            txtEstimatedDistance = view.findViewById(R.id.txt_estimated_distance);
            txtPath = view.findViewById(R.id.txt_path);
            txtDriverName = view.findViewById(R.id.txt_driver_name);
            txtRideOwnerName = view.findViewById(R.id.txt_ride_owner_name);
            txtPassengers = view.findViewById(R.id.txt_passengers);
            txtServices = view.findViewById(R.id.txt_services);
            txtCancellationReason = view.findViewById(R.id.txt_cancellation_reason);
            cardReviews = view.findViewById(R.id.card_reviews);
            reviewsContainer = view.findViewById(R.id.reviews_container);
            layoutScheduledTime = view.findViewById(R.id.layout_scheduled_time);
            layoutStartTime = view.findViewById(R.id.layout_start_time);
            layoutEndTime = view.findViewById(R.id.layout_end_time);
            layoutPrice = view.findViewById(R.id.layout_price);
            layoutTotalCost = view.findViewById(R.id.layout_total_cost);
            layoutEstimatedTime = view.findViewById(R.id.layout_estimated_time);
            layoutEstimatedDistance = view.findViewById(R.id.layout_estimated_distance);
            layoutDriver = view.findViewById(R.id.layout_driver);
            layoutRideOwner = view.findViewById(R.id.layout_ride_owner);
            layoutPassengers = view.findViewById(R.id.layout_passengers);
            layoutPath = view.findViewById(R.id.layout_path);

            cardPeople = view.findViewById(R.id.card_people);
            cardServices = view.findViewById(R.id.card_services);
            cardCancellation = view.findViewById(R.id.card_cancellation);
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
            rideModel.getRideDetails().observe(getViewLifecycleOwner(), rideTracking -> {
                if (rideTracking != null) {
                    displayRideTracking(rideTracking);
                    contentContainer.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    displayPassengerReviews(rideTracking.getPassengers());
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
            if (rideId != null) {
                // Registered user with ride ID
                Log.d(TAG, "Loading ride with ID: " + rideId);
                rideModel.loadRideById(rideId);
            } else {
                errorView.setText("No ride information provided");
                errorView.setVisibility(View.VISIBLE);
                contentContainer.setVisibility(View.GONE);
            }
        }

        private void displayRideTracking(RideDTO ride) {
            Log.d(TAG, "Displaying ride tracking for ride ID: " + ride.getId());

            txtRideId.setText("Ride #" + ride.getId());
            txtStatus.setText(formatStatus(ride.getStatus()));


            showIfNotNull(layoutScheduledTime, txtScheduledTime, formatDateTime(ride.getScheduledTime()));
            showIfNotNull(layoutStartTime, txtStartTime, formatDateTime(ride.getStartTime()));
            showIfNotNull(layoutEndTime, txtEndTime, formatDateTime(ride.getEndTime()));
            showIfNotNull(layoutPrice, txtPrice, ride.getPrice() != null ? ride.getPrice() + " RSD" : null);
            showIfNotNull(layoutTotalCost, txtTotalCost, ride.getTotalCost() != null ? ride.getTotalCost() + " RSD" : null);
            showIfNotNull(layoutPath, txtPath, ride.getPath());
            if(ride.getPath() != null && !ride.getPath().isEmpty()) {
                routeDrawer.drawRouteFromGeohashString(ride.getPath(), 12, ContextCompat.getColor(requireContext(), R.color.route_color_alternative), "route_" + ride.getId());
            }
            boolean hasPeopleInfo = false;
            if (ride.getDriverName() != null && !ride.getDriverName().isEmpty()) {
                layoutDriver.setVisibility(View.VISIBLE);
                txtDriverName.setText(ride.getDriverName());
                hasPeopleInfo = true;
            } else {
                layoutDriver.setVisibility(View.GONE);
            }
            if (ride.getRideOwnerName() != null && !ride.getRideOwnerName().isEmpty()) {
                layoutRideOwner.setVisibility(View.VISIBLE);
                txtRideOwnerName.setText(ride.getRideOwnerName());
                hasPeopleInfo = true;
            } else {
                layoutRideOwner.setVisibility(View.GONE);
            }
            if (ride.getPassengers() != null && !ride.getPassengers().isEmpty()) {
                layoutPassengers.setVisibility(View.VISIBLE);
                StringBuilder passengersList = new StringBuilder();
                for (PassengerDTO passenger : ride.getPassengers()) {
                    passengersList.append("• ").append(passenger.getEmail()).append("\n");
                }
                txtPassengers.setText(passengersList.toString().trim());
                hasPeopleInfo = true;
            } else {
                layoutPassengers.setVisibility(View.GONE);
            }
            if (ride.getAdditionalServices() != null && !ride.getAdditionalServices().isEmpty()) {
                cardServices.setVisibility(View.VISIBLE);
                StringBuilder servicesList = new StringBuilder();
                for (String service : ride.getAdditionalServices()) {
                    servicesList.append("• ").append(service).append("\n");
                }
                txtServices.setText(servicesList.toString().trim());
            } else {
                cardServices.setVisibility(View.GONE);
            }
            if (ride.getCancellationReason() != null && !ride.getCancellationReason().isEmpty()) {
                cardCancellation.setVisibility(View.VISIBLE);
                txtCancellationReason.setText(ride.getCancellationReason());
            } else {
                cardCancellation.setVisibility(View.GONE);
            }

            cardPeople.setVisibility(hasPeopleInfo ? View.VISIBLE : View.GONE);


            String status = ride.getStatus();
            if(currentUser != null && currentUser.getRole().equals("CUSTOMER")){
                setUpControlesUser(status , ride);
            }
            if(currentUser != null && currentUser.getRole().equals("DRIVER")){
                setUpControlesDriver(status , ride);
            }
            if(accessToken != null) {
                setUpContolesAccessToken(status, ride);
            }
            updateMapWithStops(ride.getLocations());
        }
        private void displayPassengerReviews(List<PassengerDTO> passengers) {
            if (passengers == null || passengers.isEmpty()) {
                cardReviews.setVisibility(View.GONE);
                return;
            }

            cardReviews.setVisibility(View.VISIBLE);
            reviewsContainer.removeAllViews(); // Clear previous if updating

            // Add the header back if you cleared the container
            TextView header = new TextView(getContext());
            header.setText("Passenger Reviews");
            header.setTextSize(18);
            header.setTypeface(null, android.graphics.Typeface.BOLD);
            header.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            reviewsContainer.addView(header);

            LayoutInflater inflater = LayoutInflater.from(getContext());

            for (PassengerDTO passenger : passengers) {
                View reviewView = inflater.inflate(R.layout.item_passanger_responses, reviewsContainer, false);
                TextView email = reviewView.findViewById(R.id.review_email);
                TextView driverRating = reviewView.findViewById(R.id.review_driver_rating);
                TextView vehicleRating = reviewView.findViewById(R.id.review_vehicle_rating);
                TextView comment = reviewView.findViewById(R.id.review_comment);
                TextView inconsistency = reviewView.findViewById(R.id.review_inconsistency);

                email.setText(passenger.getEmail());
                if(passenger.getDriverRating() != null) {
                    driverRating.setText("Driver: ⭐ " + passenger.getDriverRating() + "/5");
                }
                if(passenger.getVehicleRating() != null) {
                    vehicleRating.setText("Vehicle: ⭐ " + passenger.getVehicleRating() + "/5");
                }
                if(passenger.getDriverRating() == null && passenger.getVehicleRating() == null) {
                    driverRating.setText("No ratings");
                    vehicleRating.setVisibility(View.GONE);
                }

                comment.setText(passenger.getComment() != null ? passenger.getComment() : "No comment");
                if(comment == null || comment.getText().toString().isEmpty()) {
                    comment.setVisibility(View.GONE);
                }
                if (passenger.getInconsistencyNote() != null && !passenger.getInconsistencyNote().isEmpty()) {
                    inconsistency.setVisibility(View.VISIBLE);
                    inconsistency.setText("⚠️ Inconsistency: " + passenger.getInconsistencyNote());
                } else {
                    inconsistency.setVisibility(View.GONE);
                }

                reviewsContainer.addView(reviewView);
            }
        }
        private void setUpControlesUser(String status , RideDTO ride)
        {
            Log.d("RideDetailsFragment", "Setting up controls for status: " + status + ", accessToken: " + accessToken);
            if(status.equals("ACTIVE")) {
                actionCard.setVisibility(View.VISIBLE);
                actionFrame.setVisibility(View.VISIBLE);
                ActiveRideControls activeRideControls = ActiveRideControls.newInstance(ride.getId(), accessToken);
                getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , activeRideControls).commit();
                layoutEstimatedDistance.setVisibility(View.VISIBLE);
                layoutEstimatedTime.setVisibility(View.VISIBLE);
                txtEstimatedDistance.setVisibility(View.VISIBLE);
                txtEstimatedTime.setVisibility(View.VISIBLE);
                callback = WebSocketManager.subscribe("/topic/driver-locations/" + ride.getDriverId(), locationUpdate -> {
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
                                    List<RouteItemDTO> stops = ride.getLocations();
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
                                                txtEstimatedTime.setText(timeText);
                                                txtEstimatedDistance.setText(distanceText);
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
                DriverID = ride.getDriverId();
            }
            else if (status.equals("PENDING")) {
                Log.d("RideDetailsFragment", "Ride is pending. Access token: " + accessToken);
                if(accessToken == null && getMinutesUntilRide(ride.getScheduledTime()) >= 15) {
                    actionCard.setVisibility(View.VISIBLE);
                    actionFrame.setVisibility(View.VISIBLE);
                    PendingRideControls pendingRide = PendingRideControls.newInstance(ride.getId());
                    getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , pendingRide).commit();
                }
            }
            else if(status.equals("CANCELLED") || status.equals("PANICKED")) {
                if(accessToken == null) {
                    actionCard.setVisibility(View.VISIBLE);
                    actionFrame.setVisibility(View.VISIBLE);
                    CancelledRideControls pendingRide = CancelledRideControls.newInstance(ride, accessToken, false, true);
                    getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , pendingRide).commit();
                }
            } else if (status.equals("FINISHED") || status.equals("INTERRUPTED")) {
                if(accessToken == null) {
                    actionCard.setVisibility(View.VISIBLE);
                    actionFrame.setVisibility(View.VISIBLE);
                    CancelledRideControls pendingRide = CancelledRideControls.newInstance(ride, accessToken, true, true);
                    getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , pendingRide).commit();
                }
            }
            else if(status.equals("ACCEPTED")) {
                if(accessToken == null) {
                    actionCard.setVisibility(View.VISIBLE);
                    actionFrame.setVisibility(View.VISIBLE);
                    CancelledRideControls pendingRide = CancelledRideControls.newInstance(ride, accessToken, false, false);
                    getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , pendingRide).commit();
                }
            }
        }
        private void setUpControlesDriver(String status , RideDTO ride)
        {
            if(status.equals("ACCEPTED") || status.equals("ACTIVE")) {
                callback = WebSocketManager.subscribe("/topic/driver-locations/" + ride.getDriverId(), locationUpdate -> {
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
                                    List<RouteItemDTO> stops = ride.getLocations();
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
                                                txtEstimatedTime.setText(timeText);
                                                txtEstimatedDistance.setText(distanceText);
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
            }
            if(status.equals("ACCEPTED")) {
                actionCard.setVisibility(View.VISIBLE);
                actionFrame.setVisibility(View.VISIBLE);
                AceptedRide aceptedRide = AceptedRide.newInstance(ride.getId());
                getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , aceptedRide).commit();
            }else if(status.equals("ACTIVE")) {
                actionCard.setVisibility(View.VISIBLE);
                actionFrame.setVisibility(View.VISIBLE);
                ActiveRideDriver activeRideControls = ActiveRideDriver.newInstance(ride.getId());
                getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , activeRideControls).commit();
            }
        }
        private void setUpContolesAccessToken(String status , RideDTO ride)
        {
            Log.d("RideDetailsFragment", "Setting up controls for status: " + status + ", accessToken: " + accessToken);
            if(status.equals("ACTIVE")) {
                actionCard.setVisibility(View.VISIBLE);
                actionFrame.setVisibility(View.VISIBLE);
                ActiveRideControls activeRideControls = ActiveRideControls.newInstance(ride.getId(), accessToken);
                getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , activeRideControls).commit();
                layoutEstimatedDistance.setVisibility(View.VISIBLE);
                layoutEstimatedTime.setVisibility(View.VISIBLE);
                txtEstimatedDistance.setVisibility(View.VISIBLE);
                txtEstimatedTime.setVisibility(View.VISIBLE);
                callback = WebSocketManager.subscribe("/topic/driver-locations/" + ride.getDriverId(), locationUpdate -> {
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
                                    List<RouteItemDTO> stops = ride.getLocations();
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
                                                txtEstimatedTime.setText(timeText);
                                                txtEstimatedDistance.setText(distanceText);
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
                DriverID = ride.getDriverId();
            }
            else if (status.equals("FINISHED") || status.equals("INTERRUPTED")) {
                    actionCard.setVisibility(View.VISIBLE);
                    actionFrame.setVisibility(View.VISIBLE);
                    CancelledRideControls pendingRide = CancelledRideControls.newInstance(ride, accessToken);
                    getChildFragmentManager().beginTransaction().replace(R.id.actions_panel , pendingRide).commit();
                }
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
        private void loadUserInfo() {
            // Load user info if not using access token (registered user)
            if (accessToken == null || accessToken.isEmpty()) {
                authModel.getMeInfo().thenAccept(meInfo -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            currentUser = meInfo;
                        });
                    }
                }).exceptionally(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            currentUser = null;
                        });
                    }
                    return null;
                });
            }
        }
        private void setTextSafe(TextView textView, String text) {
            if (textView != null) {
                textView.setText(text != null ? text : "-");
            }
        }
        @Override
        public void onDestroyView() {
            Log.d("RideDetailsFragment", "onDestroyView called, unsubscribing from WebSocket");
            if (callback != null) {
                WebSocketManager.unsubscribe("/topic/driver-locations/" + DriverID , callback);
                callback = null;
            }
            super.onDestroyView();
            if (sheredLocationViewModel != null) {
                sheredLocationViewModel.clearStops();
            }
            routeDrawer.clearRoutes();
            super.onDestroyView();
        }
        @Override
        public void onStop() {
            super.onStop();
            Log.d("RideDetailsFragment", "onStop called, unsubscribing callback");
            if (callback != null && DriverID != null) {
                WebSocketManager.unsubscribe("/topic/driver-locations/" + DriverID, callback);
            }
            if(sheredLocationViewModel != null) {
                sheredLocationViewModel.clearStops();
            }
            routeDrawer.clearRoutes();
        }
        public void onPause() {
            Log.d("RideDetailsFragment", "onPause called, unsubscribing from WebSocket");
            super.onPause();
            if (callback != null && DriverID != null) {
                WebSocketManager.unsubscribe("/topic/driver-locations/" + DriverID, callback);
            }
            if(sheredLocationViewModel != null) {
                sheredLocationViewModel.clearStops();
            }
            routeDrawer.clearRoutes();
        }
        public void onResume() {
            Log.d("RideDetailsFragment", "onResume called, resubscribing to WebSocket");
            super.onResume();
            if (DriverID != null && callback != null) {
                WebSocketManager.subscribe("/topic/driver-locations/" + DriverID, callback);
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
        private void showIfNotNull(LinearLayout layout, TextView textView, String value) {
            if (value != null && !value.isEmpty()) {
                layout.setVisibility(View.VISIBLE);
                textView.setText(value);
            } else {
                layout.setVisibility(View.GONE);
            }
        }
        private void setupScrollToRefresh() {
            if (scrollView == null) return;

            scrollView.setOnTouchListener(new View.OnTouchListener() {
                private float startY;
                private boolean isDragging = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            isDragging = false;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            float currentY = event.getY();
                            float deltaY = currentY - startY;

                            // Check if we're at the top of the scroll and user is trying to scroll up more
                            if (scrollView.getScrollY() == 0 && deltaY > SCROLL_THRESHOLD && !isRefreshing) {
                                isDragging = true;
                                triggerRefresh();
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            isDragging = false;
                            break;
                    }
                    return false; // Allow ScrollView to handle the event normally
                }
            });
        }

        private void triggerRefresh() {
            if (isRefreshing) return;

            isRefreshing = true;
            Log.d(TAG, "Scroll to refresh triggered - reloading ride data");
            Toast.makeText(getContext(), "Refreshing ride data...", Toast.LENGTH_SHORT).show();

            loadRideData();

            // Reset refresh flag after a delay
            scrollView.postDelayed(() -> isRefreshing = false, 1500);
        }


    }