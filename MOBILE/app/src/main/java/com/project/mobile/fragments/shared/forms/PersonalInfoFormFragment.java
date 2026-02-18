package com.project.mobile.fragments.shared.forms;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.mobile.DTO.profile.PersonalInfo;
import com.project.mobile.R;
import com.project.mobile.helpers.ImageUrlHelper;

import lombok.Setter;

public class PersonalInfoFormFragment extends Fragment {

    private EditText etFirstName, etLastName, etPhoneNumber, etAddress, etEmail;
    private ImageView ivProfilePhoto;
    private FloatingActionButton btnUploadPhoto;

    private PersonalInfo personalInfo;
    private boolean showPhotoUpload = true;
    private boolean readonly = false;
    @Setter
    private OnPhotoSelectedListener photoSelectedListener;
    private Uri selectedImageUri;

    private ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher;

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(Uri imageUri);
    }

    public static PersonalInfoFormFragment newInstance(PersonalInfo personalInfo, boolean showPhotoUpload, boolean readonly) {
        PersonalInfoFormFragment fragment = new PersonalInfoFormFragment();
        fragment.personalInfo = personalInfo;
        fragment.showPhotoUpload = showPhotoUpload;
        fragment.readonly = readonly;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        loadImage(selectedImageUri);
                        if (photoSelectedListener != null) {
                            photoSelectedListener.onPhotoSelected(selectedImageUri); // Just pass URI
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info_form, container, false);
        
        initViews(view);
        setupPhotoUpload();
        populateData();
        
        return view;
    }

    private void initViews(View view) {
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        FrameLayout photoContainer = view.findViewById(R.id.photoContainer);

        // Set readonly state
        etFirstName.setEnabled(!readonly);
        etLastName.setEnabled(!readonly);
        etPhoneNumber.setEnabled(!readonly);
        etAddress.setEnabled(!readonly);
        etEmail.setEnabled(!readonly);

        // Hide photo upload if needed
        if (!showPhotoUpload) {
            photoContainer.setVisibility(View.GONE);
        }
        if (readonly) {
            btnUploadPhoto.setVisibility(View.GONE);
        }
    }

    private void setupPhotoUpload() {
        if (!showPhotoUpload || readonly) return;

        btnUploadPhoto.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        // Launch photo picker
        imagePickerLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void populateData() {
        if (personalInfo == null) return;

        etFirstName.setText(personalInfo.getFirstName());
        etLastName.setText(personalInfo.getLastName());
        etPhoneNumber.setText(personalInfo.getPhoneNumber());
        etAddress.setText(personalInfo.getAddress());
        etEmail.setText(personalInfo.getEmail());

        if (personalInfo.getImgSrc() != null && !personalInfo.getImgSrc().isEmpty()) {
            loadImage(personalInfo.getImgSrc());
        } else {
            ivProfilePhoto.setImageResource(R.drawable.default_avatar);
        }
    }

    private void loadImage(Object source) {
        String imageUrl;

        if (source instanceof String) {
            // Convert relative path to full URL
            imageUrl = ImageUrlHelper.getFullImageUrl((String) source);
        } else if (source instanceof Uri) {
            // For local URIs (from picker), use directly
            imageUrl = source.toString();
        } else {
            imageUrl = null;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(ivProfilePhoto);
    }

    public PersonalInfo getPersonalInfo() {
        if (personalInfo == null) personalInfo = new PersonalInfo();

        personalInfo.setFirstName(etFirstName.getText().toString());
        personalInfo.setLastName(etLastName.getText().toString());
        personalInfo.setPhoneNumber(etPhoneNumber.getText().toString());
        personalInfo.setAddress(etAddress.getText().toString());
        personalInfo.setEmail(etEmail.getText().toString());

        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
        if (getView() != null) {
            populateData();
        }
    }
}
