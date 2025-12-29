package com.project.mobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.project.mobile.R;
import com.project.mobile.UnregisterActivity;
import com.project.mobile.data.ProfileManager;
import com.project.mobile.models.UserProfile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class PersonalDataFragment extends Fragment {

    private TextInputEditText etFirstName, etLastName, etPhoneNumber, etAddress, etEmail;
    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnSavePersonal, btnChangePassword, LogoutButton;
    private ProfileManager profileManager;

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
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnSavePersonal = view.findViewById(R.id.btnSavePersonal);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        LogoutButton = view.findViewById(R.id.btnLogout);
    }

    private void loadUserProfile() {
        UserProfile profile = profileManager.getUserProfile();
        etFirstName.setText(profile.getFirstName());
        etLastName.setText(profile.getLastName());
        etPhoneNumber.setText(profile.getPhoneNumber());
        etAddress.setText(profile.getAddress());
        etEmail.setText(profile.getEmail());
    }

    private void setupListeners() {
        btnSavePersonal.setOnClickListener(v -> saveUserProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
        LogoutButton.setOnClickListener(v -> logout());
    }

    private void saveUserProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (validatePersonalData(firstName, lastName, phoneNumber, address, email)) {
            UserProfile profile = new UserProfile(firstName, lastName, phoneNumber,
                    address, email, null);
            profileManager.saveUserProfile(profile);
            Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();
        }
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

    private void changePassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty()) {
            etNewPassword.setError("Password is required");
            return;
        }

        if (newPassword.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Simulate password change
        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }
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
    }
}
