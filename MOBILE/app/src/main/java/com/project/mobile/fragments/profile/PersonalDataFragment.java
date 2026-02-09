package com.project.mobile.fragments.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.project.mobile.R;
import com.project.mobile.activities.UnregisterActivity;
import com.project.mobile.data.ProfileManager;
import com.project.mobile.models.UserProfile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class PersonalDataFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivProfileImage;
    private MaterialButton btnSelectImage;
    private TextInputEditText etFirstName, etLastName, etPhoneNumber, etAddress, etEmail;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword, LogoutButton;
    private ProgressBar progressBar;
    private ProfileManager profileManager;

    private File selectedImageFile;
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileManager = ProfileManager.getInstance(requireContext());

        initViews(view);
        loadUserProfile();
        setupListeners();
    }

    private void initViews(View view) {
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        LogoutButton = view.findViewById(R.id.btnLogout);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void loadUserProfile() {
        UserProfile profile = profileManager.getUserProfile();
        populateFields(profile);
    }

    private void setupListeners() {
        //btnSelectImage.setOnClickListener(v -> selectImage());
        //btnChangePassword.setOnClickListener(v -> changePassword());
        LogoutButton.setOnClickListener(v -> logout());
    }

//    private void selectImage() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            selectedImageUri = data.getData();
//            if (selectedImageUri != null) {
//                // Display the selected image
//                Glide.with(this)
//                        .load(selectedImageUri)
//                        .circleCrop()
//                        .into(ivProfileImage);
//
//                // Convert URI to File (you may need a helper method for this)
//                selectedImageFile = getFileFromUri(selectedImageUri);
//            }
//        }
//    }

//    private File getFileFromUri(Uri uri) {
//        return FileUtils.getFileFromUri(requireContext(), uri);
//    }

    /**
     * Returns user profile data for saving (called by parent fragment)
     */
    public UserProfile getUserProfileData() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (!validatePersonalData(firstName, lastName, phoneNumber, address, email)) {
            return null;
        }

        return new UserProfile(firstName, lastName, phoneNumber, address, email, null);
    }

    /**
     * Returns selected image file (called by parent fragment)
     */
    public File getSelectedImageFile() {
        return selectedImageFile;
    }

    /**
     * Clears selected image after successful upload
     */
    public void clearSelectedImage() {
        selectedImageFile = null;
        selectedImageUri = null;
    }

    private boolean validatePersonalData(String firstName, String lastName, String phoneNumber,
                                         String address, String email) {
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Phone number is required");
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            return false;
        }
        return true;
    }

//    private void changePassword() {
//        String currentPassword = etCurrentPassword.getText().toString().trim();
//        String newPassword = etNewPassword.getText().toString().trim();
//        String confirmPassword = etConfirmPassword.getText().toString().trim();
//
//        if (currentPassword.isEmpty()) {
//            etCurrentPassword.setError("Current password is required");
//            return;
//        }
//
//        if (newPassword.isEmpty()) {
//            etNewPassword.setError("New password is required");
//            return;
//        }
//
//        if (newPassword.length() < 8) {
//            etNewPassword.setError("Password must be at least 8 characters");
//            return;
//        }
//
//        if (!newPassword.equals(confirmPassword)) {
//            etConfirmPassword.setError("Passwords do not match");
//            return;
//        }
//
//        // Send password change request to server
//        progressBar.setVisibility(View.VISIBLE);
//        btnChangePassword.setEnabled(false);
//
//        PasswordChangeRequest request = new PasswordChangeRequest(currentPassword, newPassword);
//
//        RetrofitClient.getInstance(requireContext())
//                .getProfileApi()
//                .changePassword(request)
//                .enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        progressBar.setVisibility(View.GONE);
//                        btnChangePassword.setEnabled(true);
//
//                        if (response.isSuccessful()) {
//                            Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
//                            etCurrentPassword.setText("");
//                            etNewPassword.setText("");
//                            etConfirmPassword.setText("");
//                        } else {
//                            String errorMsg = "Failed to change password";
//                            if (response.code() == 401) {
//                                errorMsg = "Current password is incorrect";
//                            }
//                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        progressBar.setVisibility(View.GONE);
//                        btnChangePassword.setEnabled(true);
//                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    private void logout() {
        Intent intent = new Intent(getActivity(), UnregisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    public void populateFields(UserProfile profile) {
        etFirstName.setText(profile.getFirstName());
        etLastName.setText(profile.getLastName());
        etPhoneNumber.setText(profile.getPhoneNumber());
        etAddress.setText(profile.getAddress());
        etEmail.setText(profile.getEmail());

        // Load profile image if available
        if (profile.getPhotoUrl() != null && !profile.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(profile.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfileImage);
        }
    }
}
