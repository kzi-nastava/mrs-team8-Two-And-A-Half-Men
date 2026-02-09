package com.project.mobile.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.mobile.DTO.profile.ImageUploadResponse;
import com.project.mobile.DTO.profile.ProfileResponse;
import com.project.mobile.DTO.profile.ProfileUpdateRequest;
import com.project.mobile.R;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.data.ProfileManager;
import com.project.mobile.models.PendingChange;
import com.project.mobile.models.UserProfile;
import com.project.mobile.models.VehicleInfo;
import com.project.mobile.service.AuthService;
import com.project.mobile.service.ProfileService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private View pendingReviewLayout;
    private LinearLayout pendingChangesList;
    private MaterialButton btnCancelRequest;
    private MaterialButton btnSaveAll;
    private ProgressBar progressBar;
    private ProfileManager profileManager;

    private PersonalDataFragment personalDataFragment;
    private VehicleDataFragment vehicleDataFragment;

    private boolean isDriver = false;
    private ProfileResponse currentProfileData;
    private ProfileService profileService = RetrofitClient.retrofit.create(ProfileService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileManager = ProfileManager.getInstance(requireContext());

        initViews(view);
        loadProfileFromServer();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        pendingReviewLayout = view.findViewById(R.id.pendingReviewLayout);
        pendingChangesList = pendingReviewLayout.findViewById(R.id.pendingChangesList);
        btnCancelRequest = pendingReviewLayout.findViewById(R.id.btnCancelRequest);
        btnSaveAll = view.findViewById(R.id.btnSaveAll);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupViewPager() {
        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (!isDriver) {
                // Non-drivers only have personal data tab
                tab.setText("Personal data");
                tab.setIcon(R.drawable.ic_person);
            } else {
                // Drivers have both tabs
                switch (position) {
                    case 0:
                        tab.setText("Personal data");
                        tab.setIcon(R.drawable.ic_person);
                        break;
                    case 1:
                        tab.setText("Vehicle data");
                        tab.setIcon(R.drawable.ic_car);
                        break;
                }
            }
        }).attach();

        //btnSaveAll.setOnClickListener(v -> saveAllChanges());
    }

    private void loadProfileFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        btnSaveAll.setVisibility(View.GONE);

        profileService
                .getProfile()
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            currentProfileData = response.body();
                            handleProfileResponse(currentProfileData);
                        } else {
                            Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleProfileResponse(ProfileResponse profileData) {
        ProfileResponse.PersonalInfo personalInfo = profileData.getPersonalInfo();
        if (personalInfo == null) {
            Toast.makeText(getContext(), "Invalid profile data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is a driver
        isDriver = personalInfo.isDriver();

        // Save personal info to local storage
        UserProfile userProfile = new UserProfile(
                personalInfo.getFirstName(),
                personalInfo.getLastName(),
                personalInfo.getPhoneNumber(),
                personalInfo.getAddress(),
                personalInfo.getEmail(),
                personalInfo.getImgSrc()
        );
        profileManager.saveUserProfile(userProfile);

        // Save vehicle info if user is driver
        if (isDriver && profileData.getVehicleInfo() != null) {
            ProfileResponse.VehicleInfo vInfo = profileData.getVehicleInfo();
            VehicleInfo vehicleInfo = new VehicleInfo(
                    vInfo.getType(),
                    vInfo.getNumberOfSeats(),
                    vInfo.getModel(),
                    vInfo.getLicensePlate(),
                    vInfo.getAdditionalServices() != null ? vInfo.getAdditionalServices() : new ArrayList<>()
            );
            profileManager.saveVehicleInfo(vehicleInfo);
        }

        // Setup UI
        setupViewPager();
        btnSaveAll.setVisibility(View.VISIBLE);

        // Populate fragments once they're created
        viewPager.post(() -> {
            if (personalDataFragment != null) {
                personalDataFragment.populateFields(userProfile);
            }
            if (isDriver && vehicleDataFragment != null && profileData.getVehicleInfo() != null) {
                ProfileResponse.VehicleInfo vInfo = profileData.getVehicleInfo();
                VehicleInfo vehicleInfo = new VehicleInfo(
                        vInfo.getType(),
                        vInfo.getNumberOfSeats(),
                        vInfo.getModel(),
                        vInfo.getLicensePlate(),
                        vInfo.getAdditionalServices() != null ? vInfo.getAdditionalServices() : new ArrayList<>()
                );
                vehicleDataFragment.populateFields(vehicleInfo);
            }
        });

        // Handle pending changes
        // setupPendingReview(profileData);
    }

//    private void setupPendingReview(ProfileResponse profileData) {
//        if (profileData.hasPendingChanges()) {
//            pendingReviewLayout.setVisibility(View.VISIBLE);
//            displayPendingChanges(profileData.getPendingChangeRequest());
//
//            btnCancelRequest.setOnClickListener(v -> cancelPendingChanges());
//        } else {
//            pendingReviewLayout.setVisibility(View.GONE);
//        }
//    }

//    private void displayPendingChanges(ProfileResponse.PendingChangeRequest pendingChanges) {
//        pendingChangesList.removeAllViews();
//        ProfileResponse.PersonalInfo currentPersonal = currentProfileData.getPersonalInfo();
//        ProfileResponse.VehicleInfo currentVehicle = currentProfileData.getVehicleInfo();
//
//        // Compare and show changes
//        if (!pendingChanges.getFirstName().equals(currentPersonal.getFirstName())) {
//            addChangeItem("First Name", currentPersonal.getFirstName(), pendingChanges.getFirstName());
//        }
//        if (!pendingChanges.getLastName().equals(currentPersonal.getLastName())) {
//            addChangeItem("Last Name", currentPersonal.getLastName(), pendingChanges.getLastName());
//        }
//        if (!pendingChanges.getPhoneNumber().equals(currentPersonal.getPhoneNumber())) {
//            addChangeItem("Phone Number", currentPersonal.getPhoneNumber(), pendingChanges.getPhoneNumber());
//        }
//        if (!pendingChanges.getAddress().equals(currentPersonal.getAddress())) {
//            addChangeItem("Address", currentPersonal.getAddress(), pendingChanges.getAddress());
//        }
//        if (!pendingChanges.getEmail().equals(currentPersonal.getEmail())) {
//            addChangeItem("Email", currentPersonal.getEmail(), pendingChanges.getEmail());
//        }
//
//        // Image change
//        String currentImg = currentPersonal.getImgSrc();
//        String pendingImg = pendingChanges.getImgSrc();
//        if ((currentImg == null && pendingImg != null) ||
//                (currentImg != null && !currentImg.equals(pendingImg))) {
//            addChangeItem("Profile Image",
//                    currentImg != null ? "Current image" : "No image",
//                    pendingImg != null ? "New image" : "No image");
//        }
//
//        // Vehicle changes (only for drivers)
//        if (isDriver && currentVehicle != null) {
//            if (!pendingChanges.getVehicleType().equals(currentVehicle.getType())) {
//                addChangeItem("Vehicle Type", currentVehicle.getType(), pendingChanges.getVehicleType());
//            }
//            if (pendingChanges.getNumberOfSeats() != currentVehicle.getNumberOfSeats()) {
//                addChangeItem("Number of Seats",
//                        String.valueOf(currentVehicle.getNumberOfSeats()),
//                        String.valueOf(pendingChanges.getNumberOfSeats()));
//            }
//            if (!pendingChanges.getModel().equals(currentVehicle.getModel())) {
//                addChangeItem("Model", currentVehicle.getModel(), pendingChanges.getModel());
//            }
//            if (!pendingChanges.getLicensePlate().equals(currentVehicle.getLicensePlate())) {
//                addChangeItem("License Plate", currentVehicle.getLicensePlate(), pendingChanges.getLicensePlate());
//            }
//
//            // Services comparison
//            List<String> currentServices = currentVehicle.getAdditionalServices();
//            List<String> pendingServices = pendingChanges.getAdditionalServices();
//            if (currentServices != null && pendingServices != null &&
//                    !new ArrayList<>(currentServices).equals(new ArrayList<>(pendingServices))) {
//                addChangeItem("Additional Services",
//                        String.join(", ", currentServices),
//                        String.join(", ", pendingServices));
//            }
//        }
//    }

//    private void addChangeItem(String field, String oldValue, String newValue) {
//        View changeView = getLayoutInflater().inflate(
//                R.layout.item_pending_change, pendingChangesList, false);
//
//        TextView tvField = changeView.findViewById(R.id.tvChangeField);
//        TextView tvValue = changeView.findViewById(R.id.tvChangeValue);
//
//        tvField.setText(field + ":");
//        tvValue.setText(oldValue + " → " + newValue);
//
//        pendingChangesList.addView(changeView);
//    }

//    private void cancelPendingChanges() {
//        progressBar.setVisibility(View.VISIBLE);
//        btnCancelRequest.setEnabled(false);
//
//        RetrofitClient.getInstance(requireContext())
//                .getProfileApi()
//                .cancelPendingChanges()
//                .enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        progressBar.setVisibility(View.GONE);
//                        btnCancelRequest.setEnabled(true);
//
//                        if (response.isSuccessful()) {
//                            pendingReviewLayout.setVisibility(View.GONE);
//                            Toast.makeText(getContext(), "Pending changes cancelled", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getContext(), "Failed to cancel changes", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        progressBar.setVisibility(View.GONE);
//                        btnCancelRequest.setEnabled(true);
//                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void saveAllChanges() {
//        if (personalDataFragment == null) {
//            Toast.makeText(getContext(), "Please wait for fragments to load", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Get data from personal fragment
//        UserProfile userProfile = personalDataFragment.getUserProfileData();
//        File selectedImage = personalDataFragment.getSelectedImageFile();
//
//        // Validate personal data
//        if (userProfile == null) {
//            Toast.makeText(getContext(), "Please fill all required personal fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        VehicleInfo vehicleInfo = null;
//
//        // Get vehicle data only if user is a driver
//        if (isDriver) {
//            if (vehicleDataFragment == null) {
//                Toast.makeText(getContext(), "Please wait for vehicle data to load", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            vehicleInfo = vehicleDataFragment.getVehicleInfoData();
//            if (vehicleInfo == null) {
//                Toast.makeText(getContext(), "Please fill all required vehicle fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//        btnSaveAll.setEnabled(false);
//
//        // If there's an image, upload it first
//        if (selectedImage != null && selectedImage.exists()) {
//            uploadImageThenSaveProfile(userProfile, vehicleInfo, selectedImage);
//        } else {
//            saveProfile(userProfile, vehicleInfo, null);
//        }
//    }
//
//    private void uploadImageThenSaveProfile(UserProfile userProfile, VehicleInfo vehicleInfo, File imageFile) {
//        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
//
//        RetrofitClient.getInstance(requireContext())
//                .getProfileApi()
//                .uploadProfileImage(body)
//                .enqueue(new Callback<ImageUploadResponse>() {
//                    @Override
//                    public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
//                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
//                            String imgSrc = response.body().getImgSrc();
//                            saveProfile(userProfile, vehicleInfo, imgSrc);
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            btnSaveAll.setEnabled(true);
//                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
//                        progressBar.setVisibility(View.GONE);
//                        btnSaveAll.setEnabled(true);
//                        Toast.makeText(getContext(), "Error uploading image: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void saveProfile(UserProfile userProfile, VehicleInfo vehicleInfo, String profileImageUrl) {
//        ProfileUpdateRequest.PersonalData personalData = new ProfileUpdateRequest.PersonalData(
//                userProfile.getFirstName(),
//                userProfile.getLastName(),
//                userProfile.getPhoneNumber(),
//                userProfile.getAddress(),
//                userProfile.getEmail()
//        );
//
//        ProfileUpdateRequest.VehicleData vehicleData = null;
//        if (isDriver && vehicleInfo != null) {
//            vehicleData = new ProfileUpdateRequest.VehicleData(
//                    vehicleInfo.getType(),
//                    vehicleInfo.getNumberOfSeats(),
//                    vehicleInfo.getModel(),
//                    vehicleInfo.getPlateNumber(),
//                    vehicleInfo.getAdditionalServices()
//            );
//        }
//
//        ProfileUpdateRequest request = new ProfileUpdateRequest(personalData, vehicleData, profileImageUrl);
//
//        profileService
//                .updateProfile(request)
//                .enqueue(new Callback<ProfileResponse>() {
//                    @Override
//                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
//                        progressBar.setVisibility(View.GONE);
//                        btnSaveAll.setEnabled(true);
//
//                        if (response.isSuccessful() && response.body() != null) {
//                            Toast.makeText(getContext(), "Profile update request submitted for review", Toast.LENGTH_LONG).show();
//
//                            // Reload profile to get updated pending changes
//                            loadProfileFromServer();
//
//                            // Clear selected image if any
//                            if (personalDataFragment != null) {
//                                personalDataFragment.clearSelectedImage();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
//                        progressBar.setVisibility(View.GONE);
//                        btnSaveAll.setEnabled(true);
//                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private class ProfilePagerAdapter extends FragmentStateAdapter {

        public ProfilePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (!isDriver) {
                // Non-drivers only have personal data
                personalDataFragment = new PersonalDataFragment();
                return personalDataFragment;
            } else {
                // Drivers have both tabs
                switch (position) {
                    case 0:
                        personalDataFragment = new PersonalDataFragment();
                        return personalDataFragment;
                    case 1:
                        vehicleDataFragment = new VehicleDataFragment();
                        return vehicleDataFragment;
                    default:
                        personalDataFragment = new PersonalDataFragment();
                        return personalDataFragment;
                }
            }
        }

        @Override
        public int getItemCount() {
            return isDriver ? 2 : 1; // Only show 1 tab for non-drivers
        }
    }
}