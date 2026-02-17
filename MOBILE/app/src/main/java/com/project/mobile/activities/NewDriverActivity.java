package com.project.mobile.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.mobile.DTO.profile.PersonalInfo;
import com.project.mobile.DTO.profile.VehicleInfo;
import com.project.mobile.DTO.users.DriverRegistrationRequest;
import com.project.mobile.DTO.users.DriverRegistrationRequestPersonal;
import com.project.mobile.DTO.users.DriverRegistrationRequestVehicle;
import com.project.mobile.DTO.users.DriverRegistrationResponse;
import com.project.mobile.DTO.vehicles.AdditionalService;
import com.project.mobile.DTO.vehicles.VehicleOptions;
import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;
import com.project.mobile.adapters.TabsPagerAdapter;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.fragments.shared.forms.PersonalInfoFormFragment;
import com.project.mobile.fragments.shared.forms.VehicleInfoFormFragment;
import com.project.mobile.service.AdminUserService;
import com.project.mobile.service.VehicleService;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager2.widget.ViewPager2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewDriverActivity extends AppCompatActivity {

    // Services
    private AdminUserService adminUserService;
    private VehicleService vehicleService;

    // UI
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Button btnSaveDriver;

    // Fragments
    private PersonalInfoFormFragment personalInfoFragment;
    private VehicleInfoFormFragment vehicleInfoFragment;
    private TabsPagerAdapter pagerAdapter;

    // Data
    private List<VehicleType> vehicleTypes = new ArrayList<>();
    private List<AdditionalService> availableServices = new ArrayList<>();

    // State
    private boolean isSaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_driver);

        // Initialize services
        Retrofit retrofit = getRetrofitInstance(); // TODO: Implement this
        adminUserService = retrofit.create(AdminUserService.class);
        vehicleService = retrofit.create(VehicleService.class);

        initViews();
        setupToolbar();
        setupTabs();
        setupButton();
        loadVehicleOptions();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnSaveDriver = findViewById(R.id.btnSaveDriver);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.new_driver_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        pagerAdapter = new TabsPagerAdapter(this);

        // Personal info tab - no photo upload for new driver
        personalInfoFragment = PersonalInfoFormFragment.newInstance(null, false, false);
        pagerAdapter.addFragment(personalInfoFragment);

        // Vehicle info tab - empty form with no pre-selected values
        vehicleInfoFragment = VehicleInfoFormFragment.newInstance(null, vehicleTypes, availableServices, false);
        pagerAdapter.addFragment(vehicleInfoFragment);

        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? R.string.tab_personal_data : R.string.tab_vehicle_data);
        }).attach();
    }

    private void setupButton() {
        btnSaveDriver.setOnClickListener(v -> saveDriver());
    }

    private void loadVehicleOptions() {
        vehicleService.getVehicleOptions().enqueue(new Callback<VehicleOptions>() {
            @Override
            public void onResponse(@NonNull Call<VehicleOptions> call, @NonNull Response<VehicleOptions> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    vehicleTypes = response.body().getVehicleTypes();
                    availableServices = response.body().getAdditionalServices();
                    vehicleInfoFragment.setVehicleOptions(vehicleTypes, availableServices);
                } else {
                    Toast.makeText(NewDriverActivity.this,
                        R.string.error_loading_vehicle_options, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VehicleOptions> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(NewDriverActivity.this,
                    R.string.error_loading_vehicle_options, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDriver() {
        if (isSaving) return;

        PersonalInfo personalInfo = personalInfoFragment.getPersonalInfo();
        VehicleInfo vehicleInfo = vehicleInfoFragment.getVehicleInfo();

        // Basic validation
        if (personalInfo == null ||
            isNullOrEmpty(personalInfo.getFirstName()) ||
            isNullOrEmpty(personalInfo.getLastName()) ||
            isNullOrEmpty(personalInfo.getEmail()) ||
            isNullOrEmpty(personalInfo.getPhoneNumber()) ||
            isNullOrEmpty(personalInfo.getAddress())) {
            Toast.makeText(this, R.string.error_fill_personal_info, Toast.LENGTH_SHORT).show();
            viewPager.setCurrentItem(0); // Switch to personal tab
            return;
        }

        if (vehicleInfo == null ||
            isNullOrEmpty(vehicleInfo.getModel()) ||
            isNullOrEmpty(vehicleInfo.getLicensePlate()) ||
            vehicleInfo.getNumberOfSeats() <= 0) {
            Toast.makeText(this, R.string.error_fill_vehicle_info, Toast.LENGTH_SHORT).show();
            viewPager.setCurrentItem(1); // Switch to vehicle tab
            return;
        }

        isSaving = true;
        btnSaveDriver.setEnabled(false);

        // Build personal info request
        DriverRegistrationRequestPersonal personalRequest = new DriverRegistrationRequestPersonal(
            personalInfo.getFirstName(),
            personalInfo.getLastName(),
            personalInfo.getEmail(),
            personalInfo.getAddress(),
            personalInfo.getPhoneNumber()
        );

        // Build vehicle info request - map type name to ID
        long typeId = -1;
        for (VehicleType type : vehicleTypes) {
            if (type.getTypeName().equals(vehicleInfo.getType())) {
                typeId = type.getId();
                break;
            }
        }

        // Map service names to IDs
        List<Long> serviceIds = new ArrayList<>();
        for (String serviceName : vehicleInfo.getAdditionalServices()) {
            for (AdditionalService service : availableServices) {
                if (service.getName().equals(serviceName)) {
                    serviceIds.add(service.getId());
                    break;
                }
            }
        }

        DriverRegistrationRequestVehicle vehicleRequest = new DriverRegistrationRequestVehicle();
        vehicleRequest.setModel(vehicleInfo.getModel());
        vehicleRequest.setLicensePlate(vehicleInfo.getLicensePlate());
        vehicleRequest.setNumberOfSeats(vehicleInfo.getNumberOfSeats());
        vehicleRequest.setTypeId(typeId);
        vehicleRequest.setAdditionalServicesIds(serviceIds);

        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setPersonalInfo(personalRequest);
        request.setVehicleInfo(vehicleRequest);

        adminUserService.registerDriver(request).enqueue(new Callback<DriverRegistrationResponse>() {
            @Override
            public void onResponse(@NonNull Call<DriverRegistrationResponse> call,
                                   @NonNull Response<DriverRegistrationResponse> response) {
                if (!isAdded()) return;

                isSaving = false;
                btnSaveDriver.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(NewDriverActivity.this,
                        R.string.success_driver_registered, Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Users list
                } else {
                    Toast.makeText(NewDriverActivity.this,
                        R.string.error_registering_driver, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverRegistrationResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;

                isSaving = false;
                btnSaveDriver.setEnabled(true);
                Toast.makeText(NewDriverActivity.this,
                    getString(R.string.error_registering_driver) + " " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Needed because VehicleService.getVehicleOptions uses enqueue callbacks
    // This mimics fragment isAdded() check for activity lifecycle safety
    private boolean isAdded() {
        return !isDestroyed() && !isFinishing();
    }

    // TODO: Implement this to return your Retrofit instance
    private Retrofit getRetrofitInstance() {
        return RetrofitClient.retrofit;
    }
}
