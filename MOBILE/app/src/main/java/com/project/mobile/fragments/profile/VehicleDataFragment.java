package com.project.mobile.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.project.mobile.R;
import com.project.mobile.data.ProfileManager;
import com.project.mobile.models.VehicleInfo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class VehicleDataFragment extends Fragment {

    private AutoCompleteTextView spinnerVehicleType;
    private TextInputEditText etNumberOfSeats, etModel, etPlateNumber;
    private LinearLayout servicesContainer;
    private ProfileManager profileManager;
    private List<CheckBox> serviceCheckBoxes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicle_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileManager = ProfileManager.getInstance(requireContext());
        serviceCheckBoxes = new ArrayList<>();

        initViews(view);
        setupVehicleTypeSpinner();
        setupAdditionalServices();
        loadVehicleInfo();
    }

    private void initViews(View view) {
        spinnerVehicleType = view.findViewById(R.id.spinnerVehicleType);
        etNumberOfSeats = view.findViewById(R.id.etNumberOfSeats);
        etModel = view.findViewById(R.id.etModel);
        etPlateNumber = view.findViewById(R.id.etPlateNumber);
        servicesContainer = view.findViewById(R.id.servicesContainer);
    }

    private void setupVehicleTypeSpinner() {
        List<String> vehicleTypes = profileManager.getVehicleTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                vehicleTypes
        );
        spinnerVehicleType.setAdapter(adapter);
    }

    private void setupAdditionalServices() {
        List<String> services = profileManager.getAvailableServices();
        List<String> serviceIds = profileManager.getAvailableServiceIds();

        for (int i = 0; i < services.size(); i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(services.get(i));
            checkBox.setTag(serviceIds.get(i));
            checkBox.setTextColor(getResources().getColor(android.R.color.white));
            checkBox.setButtonTintList(getResources().getColorStateList(R.color.checkbox_tint));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            checkBox.setLayoutParams(params);

            servicesContainer.addView(checkBox);
            serviceCheckBoxes.add(checkBox);
        }
    }

    private void loadVehicleInfo() {
        VehicleInfo vehicleInfo = profileManager.getVehicleInfo();
        if (vehicleInfo != null) {
            populateFields(vehicleInfo);
        }
    }

    /**
     * Returns vehicle info data for saving (called by parent fragment)
     * This returns API service names (display names) instead of IDs
     */
    public VehicleInfo getVehicleInfoData() {
        String type = spinnerVehicleType.getText().toString().trim();
        String seatsStr = etNumberOfSeats.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String plateNumber = etPlateNumber.getText().toString().trim();

        if (!validateVehicleData(type, seatsStr, model, plateNumber)) {
            return null;
        }

        int numberOfSeats = Integer.parseInt(seatsStr);
        List<String> selectedServices = new ArrayList<>();

        // Convert IDs to display names for API
        for (CheckBox checkBox : serviceCheckBoxes) {
            if (checkBox.isChecked()) {
                String serviceId = (String) checkBox.getTag();
                // String displayName = ServiceMapper.idToDisplay(serviceId);
                selectedServices.add("displayName");
            }
        }

        return new VehicleInfo(type, numberOfSeats, model, plateNumber, selectedServices);
    }

    private boolean validateVehicleData(String type, String seats, String model, String plateNumber) {
        if (type.isEmpty()) {
            Toast.makeText(getContext(), "Please select vehicle type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (seats.isEmpty()) {
            etNumberOfSeats.setError("Number of seats is required");
            return false;
        }
        try {
            int seatsNum = Integer.parseInt(seats);
            if (seatsNum <= 0) {
                etNumberOfSeats.setError("Number of seats must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            etNumberOfSeats.setError("Invalid number");
            return false;
        }
        if (model.isEmpty()) {
            etModel.setError("Model is required");
            return false;
        }
        if (plateNumber.isEmpty()) {
            etPlateNumber.setError("Plate number is required");
            return false;
        }
        return true;
    }

    /**
     * Populate fields with vehicle info
     * This accepts API service names (display names) and converts them to IDs
     */
    public void populateFields(VehicleInfo vehicleInfo) {
        spinnerVehicleType.setText(vehicleInfo.getType(), false);
        etNumberOfSeats.setText(String.valueOf(vehicleInfo.getNumberOfSeats()));
        etModel.setText(vehicleInfo.getModel());
        etPlateNumber.setText(vehicleInfo.getPlateNumber());

        // Convert display names from API to IDs for checkbox matching
        List<String> enabledServices = vehicleInfo.getAdditionalServices();
        for (CheckBox checkBox : serviceCheckBoxes) {
            String serviceId = (String) checkBox.getTag();

            // Check if this service is enabled by checking both ID and display name
            boolean isEnabled = false;
            for (String enabledService : enabledServices) {
//                String enabledServiceId = ServiceMapper.displayToId(enabledService);
//                if (serviceId.equals(enabledServiceId) || serviceId.equals(enabledService)) {
//                    isEnabled = true;
//                    break;
//                }
            }

            checkBox.setChecked(isEnabled);
        }
    }
}