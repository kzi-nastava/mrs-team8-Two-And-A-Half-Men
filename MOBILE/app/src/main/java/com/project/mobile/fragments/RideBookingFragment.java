package com.project.mobile.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.mobile.DTO.Map.NominatimResult;
import com.project.mobile.DTO.Ride.*;
import com.project.mobile.DTO.routes.*;
import com.project.mobile.DTO.vehicles.*;
import com.project.mobile.FragmentTransition;
import com.project.mobile.R;
import com.project.mobile.adapters.*;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.helpers.ErrorMessageParser;
import com.project.mobile.map.MapFragment;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;
import com.project.mobile.map.mapForm.FormStops;
import com.project.mobile.map.mapForm.SearchFragment;
import com.project.mobile.service.RideService;
import com.project.mobile.service.RoutesService;
import com.project.mobile.service.VehicleService;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideBookingFragment extends Fragment {

    // Services
    private RideService rideService;
    private RoutesService routesService;
    private VehicleService vehicleService;
    
    // ViewModels
    private SheredLocationViewModel sheredLocationViewModel;
    
    // UI Components
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FloatingActionButton fabFavourites;
    private Button btnMoreOptions, btnEstimate, btnBookRide, btnAddPassenger;
    private LinearLayout moreOptionsContainer;
    private RadioGroup rgScheduleTime;
    private RadioButton rbNow, rbSpecificTime;
    private EditText etScheduledTime, etPassengerEmail;
    private RecyclerView rvPassengers, rvServices, rvVehicleTypes;
    
    // Adapters
    private PassengersAdapter passengersAdapter;
    private ServicesAdapter servicesAdapter;
    private VehicleTypesAdapter vehicleTypesAdapter;
    
    // Data
    private List<String> passengers = new ArrayList<>();
    private List<AdditionalService> availableServices = new ArrayList<>();
    private List<VehicleType> vehicleTypes = new ArrayList<>();
    private Set<Long> selectedServiceIds = new HashSet<>();
    private Long selectedVehicleTypeId = null;
    private String scheduledTime = null;
    private boolean moreOptionsExpanded = false;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sheredLocationViewModel = new ViewModelProvider(requireActivity())
            .get(SheredLocationViewModel.class);
        
        // Initialize services
        rideService = RetrofitClient.retrofit.create(RideService.class);
        routesService = RetrofitClient.retrofit.create(RoutesService.class);
        vehicleService = RetrofitClient.retrofit.create(VehicleService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupMap();
        setupSearchAndStops();
        setupBottomSheet();
        setupListeners();
        setupRecyclerViews();
        loadVehicleOptions();
    }

    private void initViews(View view) {
        // Bottom sheet
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        
        // FAB
        fabFavourites = view.findViewById(R.id.fab_favourites);
        
        // Buttons
        btnMoreOptions = view.findViewById(R.id.btn_more_options);
        btnEstimate = view.findViewById(R.id.btn_estimate);
        btnBookRide = view.findViewById(R.id.btn_book_ride);
        btnAddPassenger = view.findViewById(R.id.btn_add_passenger);
        
        // More options container
        moreOptionsContainer = view.findViewById(R.id.more_options_container);
        
        // Schedule time
        rgScheduleTime = view.findViewById(R.id.rg_schedule_time);
        rbNow = view.findViewById(R.id.rb_now);
        rbSpecificTime = view.findViewById(R.id.rb_specific_time);
        etScheduledTime = view.findViewById(R.id.et_scheduled_time);
        
        // Passengers
        etPassengerEmail = view.findViewById(R.id.et_passenger_email);
        rvPassengers = view.findViewById(R.id.rv_passengers);
        
        // Services & Vehicle Types
        rvServices = view.findViewById(R.id.rv_services);
        rvVehicleTypes = view.findViewById(R.id.rv_vehicle_types);
    }

    private void setupMap() {
        getChildFragmentManager().beginTransaction()
            .replace(R.id.map_container, new MapFragment(true))
            .commit();
    }

    private void setupSearchAndStops() {
        getChildFragmentManager().beginTransaction()
            .replace(R.id.search_container, new SearchFragment())
            .replace(R.id.stops_container, new FormStops()) // Unlimited stops
            .commit();
    }

    private void setupBottomSheet() {
        // Set peek height (30-40% of screen)
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        
        // Prevent dragging to hide
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED ||
                        newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fabFavourites.show();
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    fabFavourites.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Optional animations
            }
        });
    }

    private void setupListeners() {
        // More Options
        btnMoreOptions.setOnClickListener(v -> toggleMoreOptions());
        
        // Estimate
        btnEstimate.setOnClickListener(v -> getEstimate());
        
        // Book Ride
        btnBookRide.setOnClickListener(v -> bookRide());
        
        // Add Passenger
        btnAddPassenger.setOnClickListener(v -> addPassenger());
        
        // Schedule Time
        rgScheduleTime.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_specific_time) {
                etScheduledTime.setVisibility(View.VISIBLE);
            } else {
                etScheduledTime.setVisibility(View.GONE);
                scheduledTime = null;
            }
        });
        
        // DateTime Picker
        etScheduledTime.setOnClickListener(v -> showDateTimePicker());
        
        // Favourites FAB
        fabFavourites.setOnClickListener(v -> showFavouritesDialog());
    }

    private void setupRecyclerViews() {
        // Passengers
        passengersAdapter = new PassengersAdapter(passengers, this::removePassenger);
        rvPassengers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPassengers.setAdapter(passengersAdapter);
        
        // Services
        servicesAdapter = new ServicesAdapter(
            availableServices, 
            selectedServiceIds,
            this::toggleService
        );
        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvServices.setAdapter(servicesAdapter);
        
        // Vehicle Types
        vehicleTypesAdapter = new VehicleTypesAdapter(
            vehicleTypes,
            selectedVehicleTypeId,
            this::selectVehicleType
        );
        rvVehicleTypes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVehicleTypes.setAdapter(vehicleTypesAdapter);
    }

    private void toggleMoreOptions() {
        moreOptionsExpanded = !moreOptionsExpanded;
        moreOptionsContainer.setVisibility(
            moreOptionsExpanded ? View.VISIBLE : View.GONE
        );
        btnMoreOptions.setText(
            moreOptionsExpanded ? "Less options" : "More options"
        );
    }

    private void getEstimate() {
        RideBookingParametersDTO request = buildBookingRequest();
        
        if (request.getRoute() == null || request.getRoute().size() < 2) {
            Toast.makeText(getContext(), 
                "Please select at least start and destination", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        rideService.estimateRide(request).enqueue(new Callback<CostTimeDTO>() {
            @Override
            public void onResponse(Call<CostTimeDTO> call, Response<CostTimeDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CostTimeDTO estimate = response.body();
                    String message = String.format(Locale.US,
                        "Estimated time: %.0f minutes",
                        estimate.getTime());
                    
                    new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Ride Estimate")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
                } else {
                    String errorMessage = ErrorMessageParser.getErrorMessage(response);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CostTimeDTO> call, Throwable t) {
                Toast.makeText(getContext(), 
                    "Error: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    // UPDATED bookRide() method - replace in RideBookingFragment.java

    private void bookRide() {
        RideBookingParametersDTO request = buildBookingRequest();

        if (request.getRoute() == null || request.getRoute().size() < 2) {
            Toast.makeText(getContext(),
                    "Please select at least start and destination",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // If scheduled for specific time, book directly without estimate
        if (request.getScheduledTime() != null) {
            callBookAPI(request);
            return;
        }

        // For immediate rides, get estimate first then ask for confirmation
        rideService.estimateRide(request).enqueue(new Callback<CostTimeDTO>() {
            @Override
            public void onResponse(Call<CostTimeDTO> call, Response<CostTimeDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CostTimeDTO estimate = response.body();
                    int estimatedMinutes = (int) Math.ceil(estimate.getTime());

                    // Show confirmation dialog with estimate
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Ride")
                            .setMessage(String.format(Locale.US,
                                    "The estimated time for your ride is %d minutes. Do you want to proceed with booking?",
                                    estimatedMinutes))
                            .setPositiveButton("Book Ride", (dialog, which) -> {
                                // User confirmed - proceed with booking
                                callBookAPI(request);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    String errorMessage = ErrorMessageParser.getErrorMessage(response);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CostTimeDTO> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to actually call the booking API
    private void callBookAPI(RideBookingParametersDTO request) {
        rideService.bookRide(request).enqueue(new Callback<RideBookingResponse>() {
            @Override
            public void onResponse(Call<RideBookingResponse> call, Response<RideBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RideBookingResponse booking = response.body();

                    new AlertDialog.Builder(getContext())
                            .setTitle("Ride Booked!")
                            .setMessage("Your ride has been successfully booked.")
                            .setPositiveButton("View Details", (dialog, which) -> {
                                // Navigate to ride details
                                getParentFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container_view_tag,
                                                RideDetailsFragmentActive.newInstanceWithId(booking.getId()))
                                        .addToBackStack(null)
                                        .commit();
                            })
                            .setNegativeButton("OK", null)
                            .show();

                    // Clear form after successful booking
                    resetForm();
                } else {
                    String errorMessage = ErrorMessageParser.getErrorMessage(response);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RideBookingResponse> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private RideBookingParametersDTO buildBookingRequest() {
        RideBookingParametersDTO request = new RideBookingParametersDTO();
        
        // Route
        List<NominatimResult> stops = sheredLocationViewModel.getStops().getValue();
        if (stops != null && !stops.isEmpty()) {
            List<RouteItemDTO> route = new ArrayList<>();
            for (NominatimResult stop : stops) {
                route.add(new RouteItemDTO(stop));
            }
            request.setRoute(route);
        }
        
        // Scheduled time
        if (rbSpecificTime.isChecked() && scheduledTime != null) {
            request.setScheduledTime(scheduledTime);
        }
        
        // Passengers
        if (!passengers.isEmpty()) {
            request.setPassengers(passengers);
        }
        
        // Vehicle type
        if (selectedVehicleTypeId != null) {
            request.setVehicleTypeId(selectedVehicleTypeId);
        }
        
        // Services
        if (!selectedServiceIds.isEmpty()) {
            request.setAdditionalServicesIds(new ArrayList<>(selectedServiceIds));
        }
        
        return request;
    }

    private void addPassenger() {
        String email = etPassengerEmail.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        
        passengers.add(email);
        passengersAdapter.notifyDataSetChanged();
        etPassengerEmail.setText("");
    }

    private void removePassenger(int position) {
        passengers.remove(position);
        passengersAdapter.notifyDataSetChanged();
    }

    private void toggleService(long serviceId) {
        if (selectedServiceIds.contains(serviceId)) {
            selectedServiceIds.remove(serviceId);
        } else {
            selectedServiceIds.add(serviceId);
        }
        servicesAdapter.notifyDataSetChanged();
    }

    private void selectVehicleType(long typeId) {
        selectedVehicleTypeId = typeId;
        vehicleTypesAdapter.updateSelection(typeId);
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            
            new TimePickerDialog(getContext(), (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                scheduledTime = sdf.format(calendar.getTime());
                
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
                etScheduledTime.setText(displayFormat.format(calendar.getTime()));
                
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showFavouritesDialog() {
        routesService.getFavouriteRoutes().enqueue(new Callback<FavouriteRoutesResponse>() {
            @Override
            public void onResponse(Call<FavouriteRoutesResponse> call, Response<FavouriteRoutesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FavouriteRoute> routes = response.body().getRoutes();
                    
                    if (routes.isEmpty()) {
                        Toast.makeText(getContext(), "No favourite routes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    showFavouriteRoutesDialog(routes);
                } else {
                    String errorMessage = ErrorMessageParser.getErrorMessage(response);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FavouriteRoutesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFavouriteRoutesDialog(List<FavouriteRoute> routes) {
        // Create dialog with RecyclerView
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_favourite_routes, null);
        RecyclerView rvFavourites = dialogView.findViewById(R.id.rv_favourite_routes);
        
        FavouriteRoutesAdapter adapter = new FavouriteRoutesAdapter(routes, route -> {
            // Load selected route into form
            loadFavouriteRoute(route);
        });
        
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavourites.setAdapter(adapter);
        
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Favourite Routes")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void loadFavouriteRoute(FavouriteRoute route) {
        // Clear current stops
        sheredLocationViewModel.clearStops();
        
        // Add all points from favourite route
        List<RouteItemDTO> points = route.getPoints();
        for (RouteItemDTO point : points) {
            NominatimResult result = new NominatimResult(
                point.getAddress(),
                point.getLatitude(),
                point.getLongitude()
            );
            sheredLocationViewModel.addLocation(result);
        }
        
        Toast.makeText(getContext(), "Favourite route loaded", Toast.LENGTH_SHORT).show();
    }

    private void loadVehicleOptions() {
        vehicleService.getVehicleOptions().enqueue(new Callback<VehicleOptions>() {
            @Override
            public void onResponse(Call<VehicleOptions> call, Response<VehicleOptions> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VehicleOptions options = response.body();
                    
                    availableServices.clear();
                    availableServices.addAll(options.getAdditionalServices());
                    servicesAdapter.notifyDataSetChanged();
                    
                    vehicleTypes.clear();
                    vehicleTypes.addAll(options.getVehicleTypes());
                    vehicleTypesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<VehicleOptions> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load vehicle options", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        sheredLocationViewModel.clearStops();
        passengers.clear();
        passengersAdapter.notifyDataSetChanged();
        selectedServiceIds.clear();
        servicesAdapter.notifyDataSetChanged();
        selectedVehicleTypeId = null;
        vehicleTypesAdapter.updateSelection(null);
        scheduledTime = null;
        etScheduledTime.setText("");
        rbNow.setChecked(true);
        moreOptionsExpanded = false;
        moreOptionsContainer.setVisibility(View.GONE);
        btnMoreOptions.setText("More options");
    }
}
