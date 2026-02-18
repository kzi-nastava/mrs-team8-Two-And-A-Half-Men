package com.project.mobile.fragments.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.mobile.DTO.profile.CancelRequestResponse;
import com.project.mobile.DTO.profile.ImageUploadResponse;
import com.project.mobile.DTO.profile.PasswordChangeRequest;
import com.project.mobile.DTO.profile.PendingChangeRequest;
import com.project.mobile.DTO.profile.PersonalInfo;
import com.project.mobile.DTO.profile.ProfileResponse;
import com.project.mobile.DTO.profile.ProfileUpdateRequest;
import com.project.mobile.DTO.profile.ProfileUpdateResponse;
import com.project.mobile.DTO.profile.VehicleInfo;
import com.project.mobile.DTO.vehicles.AdditionalService;
import com.project.mobile.DTO.vehicles.VehicleOptions;
import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;
import com.project.mobile.activities.UnregisterActivity;
import com.project.mobile.adapters.PendingChangesAdapter;
import com.project.mobile.adapters.TabsPagerAdapter;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.fragments.shared.forms.PersonalInfoFormFragment;
import com.project.mobile.fragments.shared.forms.VehicleInfoFormFragment;
import com.project.mobile.helpers.DialogHelper;
import com.project.mobile.managers.NotificationManager;
import com.project.mobile.service.ActivityService;
import com.project.mobile.service.ProfileService;
import com.project.mobile.service.VehicleService;
import com.project.mobile.viewModels.AuthModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfilePageFragment extends Fragment {

    // Services
    private ProfileService profileService;
    private VehicleService vehicleService;

    // UI Components
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Button btnSave, btnChangePassword, btnLogout;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private LinearLayout blockedBanner, pendingChangesPreview, workingStatusLayout;
    private TextView tvBlockReason;
    private Button btnCancelRequest;

    private Button btnToggleStopWorking;

    // Fragments
    private PersonalInfoFormFragment personalInfoFragment;
    private VehicleInfoFormFragment vehicleInfoFragment;

    // Data
    private PersonalInfo personalInfo;
    private PersonalInfo originalPersonalInfo;
    private VehicleInfo vehicleInfo;
    private VehicleInfo originalVehicleInfo;
    private PendingChangeRequest pendingChangeRequest;
    private List<VehicleType> vehicleTypes = new ArrayList<>();
    private List<AdditionalService> availableServices = new ArrayList<>();
    private PendingChangesAdapter pendingChangesAdapter;
    private TabsPagerAdapter pagerAdapter;
    private AuthModel authModel;
    private ActivityService activityService;
    private Button btnToggleWorking;
    // State
    private boolean isSaving = false;
    private Uri selectedImageUri;

    private NotificationManager notificationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize managers
        notificationManager = NotificationManager.getInstance(this.getContext());
        authModel = new ViewModelProvider(this).get(AuthModel.class);

        // Initialize services (get from Retrofit instance)
        Retrofit retrofit = getRetrofitInstance(); // You need to implement this
        profileService = retrofit.create(ProfileService.class);
        vehicleService = retrofit.create(VehicleService.class);
        activityService = retrofit.create(ActivityService.class);
        authModel = new ViewModelProvider(this).get(AuthModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);
        
        initViews(view);
        setupButtons();
        loadProfileData();
        
        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        btnSave = view.findViewById(R.id.btnSave);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        blockedBanner = view.findViewById(R.id.blockedBanner);
        pendingChangesPreview = view.findViewById(R.id.pendingChangesPreview);
        tvBlockReason = view.findViewById(R.id.tvBlockReason);
        btnCancelRequest = view.findViewById(R.id.btnCancelRequest);
        btnToggleWorking = view.findViewById(R.id.btn_driver_activate);
        btnToggleStopWorking = view.findViewById(R.id.btn_driver_deactivate);
        RecyclerView rvPendingChanges = view.findViewById(R.id.rvPendingChanges);
        workingStatusLayout = view.findViewById(R.id.driver_activity_controls);
        // Setup pending changes RecyclerView
        pendingChangesAdapter = new PendingChangesAdapter();
        rvPendingChanges.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPendingChanges.setAdapter(pendingChangesAdapter);
    }

    private void setupButtons() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnCancelRequest.setOnClickListener(v -> cancelPendingRequest());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadProfileData() {
        profileService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateProfileData(response.body());
                } else {
                    Toast.makeText(requireContext(), R.string.error_loading_profile, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), 
                    getString(R.string.error_loading_profile) + " " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileData(ProfileResponse profile) {
        personalInfo = profile.getPersonalInfo();
        originalPersonalInfo = clonePersonalInfo(personalInfo);

        vehicleInfo = profile.getVehicleInfo();
        if (vehicleInfo != null) {
            originalVehicleInfo = cloneVehicleInfo(vehicleInfo);
            loadVehicleOptions();
        }

        pendingChangeRequest = profile.getPendingChangeRequest();

        // Check if this is initial load or update
        if (pagerAdapter == null) {
            // Initial load - setup everything
            setupTabs();
        } else {
            // Update - just refresh the existing fragments
            if (personalInfoFragment != null) {
                personalInfoFragment.setPersonalInfo(personalInfo);
            }
            if (vehicleInfoFragment != null && vehicleInfo != null) {
                vehicleInfoFragment.setVehicleInfo(vehicleInfo);
            }
        }

        setupBlockedBanner();
        setupPendingChanges();
        setUpActivity(profile);
    }
    private void setupTabs() {
        pagerAdapter = new TabsPagerAdapter(this);
        // Personal Info Tab
        personalInfoFragment = PersonalInfoFormFragment.newInstance(personalInfo, true, false);
        personalInfoFragment.setPhotoSelectedListener(uri -> {
            selectedImageUri = uri;
        });
        pagerAdapter.addFragment(personalInfoFragment);
        
        // Vehicle Tab (only if driver)
        if (vehicleInfo != null) {
            vehicleInfoFragment = VehicleInfoFormFragment.newInstance(
                vehicleInfo, vehicleTypes, availableServices, false
            );
            pagerAdapter.addFragment(vehicleInfoFragment);
        }
        
        viewPager.setAdapter(pagerAdapter);
        
        // Setup tab titles
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.tab_personal_data);
            } else {
                tab.setText(R.string.tab_vehicle_data);
            }
        }).attach();
    }

    private void setupBlockedBanner() {
        if (personalInfo.isBlocked() && personalInfo.getBlockReason() != null) {
            blockedBanner.setVisibility(View.VISIBLE);
            tvBlockReason.setText(personalInfo.getBlockReason());
        } else {
            blockedBanner.setVisibility(View.GONE);
        }
    }

    private void setupPendingChanges() {
        if (pendingChangeRequest != null) {
            pendingChangesPreview.setVisibility(View.VISIBLE);
            
            List<PendingChangesAdapter.ChangeItem> changes = buildPendingChangesList();
            pendingChangesAdapter.setChanges(changes);
        } else {
            pendingChangesPreview.setVisibility(View.GONE);
        }
    }

    private List<PendingChangesAdapter.ChangeItem> buildPendingChangesList() {
        List<PendingChangesAdapter.ChangeItem> changes = new ArrayList<>();
        
        if (pendingChangeRequest == null) return changes;
        
        addChangeIfDifferent(changes, getString(R.string.change_field_first_name), 
            originalPersonalInfo.getFirstName(), pendingChangeRequest.getFirstName());
        addChangeIfDifferent(changes, getString(R.string.change_field_last_name), 
            originalPersonalInfo.getLastName(), pendingChangeRequest.getLastName());
        addChangeIfDifferent(changes, getString(R.string.change_field_phone), 
            originalPersonalInfo.getPhoneNumber(), pendingChangeRequest.getPhoneNumber());
        addChangeIfDifferent(changes, getString(R.string.change_field_address), 
            originalPersonalInfo.getAddress(), pendingChangeRequest.getAddress());
        addChangeIfDifferent(changes, getString(R.string.change_field_email), 
            originalPersonalInfo.getEmail(), pendingChangeRequest.getEmail());
        
        if (originalVehicleInfo != null) {
            addChangeIfDifferent(changes, getString(R.string.change_field_vehicle_type), 
                originalVehicleInfo.getType(), pendingChangeRequest.getVehicleType());
            addChangeIfDifferent(changes, getString(R.string.change_field_model), 
                originalVehicleInfo.getModel(), pendingChangeRequest.getModel());
            addChangeIfDifferent(changes, getString(R.string.change_field_license_plate), 
                originalVehicleInfo.getLicensePlate(), pendingChangeRequest.getLicensePlate());
            addChangeIfDifferent(changes, getString(R.string.change_field_seats), 
                String.valueOf(originalVehicleInfo.getNumberOfSeats()), 
                String.valueOf(pendingChangeRequest.getNumberOfSeats()));
        }
        
        // Image change
        if (pendingChangeRequest.getImgSrc() != null && 
            !pendingChangeRequest.getImgSrc().equals(originalPersonalInfo.getImgSrc())) {
            changes.add(new PendingChangesAdapter.ChangeItem(
                getString(R.string.change_field_profile_image),
                originalPersonalInfo.getImgSrc(),
                pendingChangeRequest.getImgSrc(),
                true
            ));
        }
        
        return changes;
    }

    private void addChangeIfDifferent(List<PendingChangesAdapter.ChangeItem> changes, 
                                     String field, String oldValue, String newValue) {
        if (newValue != null && !newValue.equals(oldValue)) {
            changes.add(new PendingChangesAdapter.ChangeItem(field, oldValue, newValue, false));
        }
    }

    private void loadVehicleOptions() {
        vehicleService.getVehicleOptions().enqueue(new Callback<VehicleOptions>() {
            @Override
            public void onResponse(@NonNull Call<VehicleOptions> call, @NonNull Response<VehicleOptions> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicleTypes = response.body().getVehicleTypes();
                    availableServices = response.body().getAdditionalServices();
                    
                    if (vehicleInfoFragment != null) {
                        vehicleInfoFragment.setVehicleOptions(vehicleTypes, availableServices);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VehicleOptions> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.error_loading_vehicle_options, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (isSaving) return;

        isSaving = true;
        btnSave.setEnabled(false);

        // Get updated data from fragments
        PersonalInfo updatedPersonalInfo = personalInfoFragment.getPersonalInfo();
        VehicleInfo updatedVehicleInfo = null;
        if (vehicleInfoFragment != null) {
            updatedVehicleInfo = vehicleInfoFragment.getVehicleInfo();
        }

        // Upload image first if selected
        if (selectedImageUri != null) { // Changed from selectedImageFile
            uploadImage(updatedPersonalInfo, updatedVehicleInfo);
        } else {
            updateProfile(updatedPersonalInfo, updatedVehicleInfo, null);
        }
    }
    private void uploadImage(PersonalInfo personalInfo, VehicleInfo vehicleInfo) {
        try {
            // Get InputStream from content URI
            InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
            if (inputStream == null) {
                Toast.makeText(requireContext(), R.string.error_uploading_photo, Toast.LENGTH_SHORT).show();
                isSaving = false;
                btnSave.setEnabled(true);
                return;
            }

            // Read bytes from InputStream
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            // Create RequestBody from bytes
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);

            // Get filename from URI
            String filename = "profile_image_" + System.currentTimeMillis() + ".jpg";
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, requestFile);

            profileService.uploadProfileImage(body).enqueue(new Callback<ImageUploadResponse>() {
                @Override
                public void onResponse(@NonNull Call<ImageUploadResponse> call,
                                       @NonNull Response<ImageUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                        String imagePath = response.body().getFilePath();
                        updateProfile(personalInfo, vehicleInfo, imagePath);
                    } else {
                        isSaving = false;
                        btnSave.setEnabled(true);
                        Toast.makeText(requireContext(), R.string.error_uploading_photo, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ImageUploadResponse> call, @NonNull Throwable t) {
                    isSaving = false;
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(),
                            getString(R.string.error_uploading_photo) + " " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            isSaving = false;
            btnSave.setEnabled(true);
            Toast.makeText(requireContext(),
                    getString(R.string.error_uploading_photo) + " " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void updateProfile(PersonalInfo personalInfo, VehicleInfo vehicleInfo, String imagePath) {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFirstName(personalInfo.getFirstName());
        request.setLastName(personalInfo.getLastName());
        request.setEmail(personalInfo.getEmail());
        request.setAddress(personalInfo.getAddress());
        request.setPhoneNumber(personalInfo.getPhoneNumber());
        request.setImgSrc(imagePath != null ? imagePath : personalInfo.getImgSrc());
        
        if (vehicleInfo != null) {
            request.setModel(vehicleInfo.getModel());
            request.setLicensePlate(vehicleInfo.getLicensePlate());
            request.setNumberOfSeats(vehicleInfo.getNumberOfSeats());
            
            // Find vehicle type ID
            for (VehicleType type : vehicleTypes) {
                if (type.getTypeName().equals(vehicleInfo.getType())) {
                    request.setVehicleTypeId(type.getId());
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
            request.setAdditionalServiceIds(serviceIds);
        }
        
        profileService.updateProfile(request).enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileUpdateResponse> call, @NonNull Response<ProfileUpdateResponse> response) {
                isSaving = false;
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), R.string.success_profile_updated_message, Toast.LENGTH_SHORT).show();
                    if(response.body().getProfile() != null && response.body().getProfile().getAccessToken() != null) {
                        authModel.updateJwtToken(response.body().getProfile().getAccessToken());
                    }
                    updateProfileData(response.body().getProfile());
                    selectedImageUri = null;
                } else {
                    Toast.makeText(requireContext(), R.string.error_updating_profile, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileUpdateResponse> call, @NonNull Throwable t) {
                isSaving = false;
                btnSave.setEnabled(true);
                Toast.makeText(requireContext(), 
                    getString(R.string.error_updating_profile) + " " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(requireContext(), R.string.error_passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (newPassword.length() < 8) {
            Toast.makeText(requireContext(), R.string.error_password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }
        
        isSaving = true;
        btnChangePassword.setEnabled(false);
        
        PasswordChangeRequest request = new PasswordChangeRequest(oldPassword, newPassword, confirmPassword);
        
        profileService.changePassword(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                isSaving = false;
                btnChangePassword.setEnabled(true);
                
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.success_password_changed_message, Toast.LENGTH_SHORT).show();
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");
                } else {
                    Toast.makeText(requireContext(), R.string.error_changing_password, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                isSaving = false;
                btnChangePassword.setEnabled(true);
                Toast.makeText(requireContext(), 
                    getString(R.string.error_changing_password) + " " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelPendingRequest() {
        if (pendingChangeRequest == null) return;
        
        DialogHelper.showConfirmDialog(
            requireContext(),
            getString(R.string.dialog_cancel_request_title),
            getString(R.string.dialog_cancel_request_message),
            () -> {
                profileService.cancelPendingChanges(pendingChangeRequest.getId())
                    .enqueue(new Callback<CancelRequestResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CancelRequestResponse> call, 
                                             @NonNull Response<CancelRequestResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isOk()) {
                                pendingChangeRequest = null;
                                setupPendingChanges();
                                Toast.makeText(requireContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CancelRequestResponse> call, @NonNull Throwable t) {
                            Toast.makeText(requireContext(), "Failed to cancel request", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        );
    }

    // Helper methods for cloning objects
    private PersonalInfo clonePersonalInfo(PersonalInfo original) {
        PersonalInfo clone = new PersonalInfo();
        clone.setId(original.getId());
        clone.setFirstName(original.getFirstName());
        clone.setLastName(original.getLastName());
        clone.setPhoneNumber(original.getPhoneNumber());
        clone.setAddress(original.getAddress());
        clone.setEmail(original.getEmail());
        clone.setImgSrc(original.getImgSrc());
        clone.setRole(original.getRole());
        clone.setBlocked(original.isBlocked());
        clone.setBlockReason(original.getBlockReason());
        return clone;
    }

    private VehicleInfo cloneVehicleInfo(VehicleInfo original) {
        VehicleInfo clone = new VehicleInfo();
        clone.setId(original.getId());
        clone.setType(original.getType());
        clone.setNumberOfSeats(original.getNumberOfSeats());
        clone.setModel(original.getModel());
        clone.setLicensePlate(original.getLicensePlate());
        clone.setAdditionalServices(new ArrayList<>(original.getAdditionalServices()));
        return clone;
    }
    private void setUpActivity(ProfileResponse profile)
    {
        if(!profile.isDriver())
            return;
        if(profile.getIsWorking() == null)
            return;
        workingStatusLayout.setVisibility(View.VISIBLE);
        if(profile.getIsWorking())
        {
            btnToggleWorking.setVisibility(View.GONE);
            btnToggleStopWorking.setVisibility(View.VISIBLE);
            btnToggleStopWorking.setOnClickListener(v -> {
                activityService.stopWorkingStatus().enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if(response.isSuccessful())
                        {
                            Toast.makeText(requireContext(), "Working stop", Toast.LENGTH_SHORT).show();
                            loadProfileData();
                        }
                        else
                        {
                            try {
                                String errorMsg = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "Unknown error";
                                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(),   "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            btnToggleStopWorking.setVisibility(View.GONE);
            btnToggleWorking.setVisibility(View.VISIBLE);
            btnToggleWorking.setOnClickListener(v -> {
                activityService.startWorkingStatus().enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if(response.isSuccessful())
                        {
                            Toast.makeText(requireContext(), "Working start", Toast.LENGTH_SHORT).show();
                            loadProfileData();
                        }
                        else
                        {
                            try {
                                String errorMsg = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "Unknown error";
                                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(),   "Error " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private Retrofit getRetrofitInstance() {
        return RetrofitClient.retrofit;
    }
    private void logout() {
        notificationManager.cleanup();
        notificationManager.unsubscribeFromNotifications();
        Intent intent = new Intent(getActivity(), UnregisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        assert getActivity() != null;
        authModel.logout();
        getActivity().finish();
    }
}
