package com.project.mobile.fragments.shared.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.profile.VehicleInfo;
import com.project.mobile.DTO.vehicles.AdditionalService;
import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;
import com.project.mobile.adapters.AdditionalServicesAdapter;

import java.util.ArrayList;
import java.util.List;

public class VehicleInfoFormFragment extends Fragment {

    private Spinner spinnerVehicleType;
    private TextView tvVehicleTypeDescription;
    private EditText etModel, etLicensePlate, etNumberOfSeats;
    private RecyclerView rvAdditionalServices;
    
    private VehicleInfo vehicleInfo;
    private List<VehicleType> vehicleTypes = new ArrayList<>();
    private List<AdditionalService> availableServices = new ArrayList<>();
    private AdditionalServicesAdapter servicesAdapter;
    private boolean readonly = false;

    public static VehicleInfoFormFragment newInstance(VehicleInfo vehicleInfo, 
                                                      List<VehicleType> vehicleTypes,
                                                      List<AdditionalService> availableServices,
                                                      boolean readonly) {
        VehicleInfoFormFragment fragment = new VehicleInfoFormFragment();
        fragment.vehicleInfo = vehicleInfo;
        fragment.vehicleTypes = vehicleTypes;
        fragment.availableServices = availableServices;
        fragment.readonly = readonly;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_info_form, container, false);
        
        initViews(view);
        setupVehicleTypes();
        setupAdditionalServices();
        populateData();
        
        return view;
    }

    private void initViews(View view) {
        spinnerVehicleType = view.findViewById(R.id.spinnerVehicleType);
        tvVehicleTypeDescription = view.findViewById(R.id.tvVehicleTypeDescription);
        etModel = view.findViewById(R.id.etModel);
        etLicensePlate = view.findViewById(R.id.etLicensePlate);
        etNumberOfSeats = view.findViewById(R.id.etNumberOfSeats);
        rvAdditionalServices = view.findViewById(R.id.rvAdditionalServices);

        etModel.setEnabled(!readonly);
        etLicensePlate.setEnabled(!readonly);
        etNumberOfSeats.setEnabled(!readonly);
        spinnerVehicleType.setEnabled(!readonly);
    }

    private void setupVehicleTypes() {
        List<String> typeNames = new ArrayList<>();
        for (VehicleType type : vehicleTypes) {
            typeNames.add(type.getTypeName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(), 
            android.R.layout.simple_spinner_item, 
            typeNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);
        
        spinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < vehicleTypes.size()) {
                    VehicleType selectedType = vehicleTypes.get(position);
                    tvVehicleTypeDescription.setText(selectedType.getDescription());
                    tvVehicleTypeDescription.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvVehicleTypeDescription.setVisibility(View.GONE);
            }
        });
    }

    private void setupAdditionalServices() {
        servicesAdapter = new AdditionalServicesAdapter();
        servicesAdapter.setServices(availableServices);
        servicesAdapter.setReadonly(readonly);
        
        rvAdditionalServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAdditionalServices.setAdapter(servicesAdapter);
    }

    private void populateData() {
        if (vehicleInfo == null) return;

        // Set vehicle type
        for (int i = 0; i < vehicleTypes.size(); i++) {
            if (vehicleTypes.get(i).getTypeName().equals(vehicleInfo.getType())) {
                spinnerVehicleType.setSelection(i);
                break;
            }
        }

        etModel.setText(vehicleInfo.getModel());
        etLicensePlate.setText(vehicleInfo.getLicensePlate());
        if (vehicleInfo.getNumberOfSeats() > 0) {
            etNumberOfSeats.setText(String.valueOf(vehicleInfo.getNumberOfSeats()));
        }

        servicesAdapter.setSelectedServices(vehicleInfo.getAdditionalServices());
    }

    public VehicleInfo getVehicleInfo() {
        // Add this null check at the very beginning
        if (spinnerVehicleType == null) {
            // View not created yet, return current vehicleInfo
            return vehicleInfo;
        }

        if (vehicleInfo == null) vehicleInfo = new VehicleInfo();

        int selectedPosition = spinnerVehicleType.getSelectedItemPosition();
        if (selectedPosition >= 0 && selectedPosition < vehicleTypes.size()) {
            vehicleInfo.setType(vehicleTypes.get(selectedPosition).getTypeName());
        }

        vehicleInfo.setModel(etModel.getText().toString());
        vehicleInfo.setLicensePlate(etLicensePlate.getText().toString());

        String seatsText = etNumberOfSeats.getText().toString();
        if (!seatsText.isEmpty()) {
            try {
                vehicleInfo.setNumberOfSeats(Integer.parseInt(seatsText));
            } catch (NumberFormatException e) {
                vehicleInfo.setNumberOfSeats(0);
            }
        }

        vehicleInfo.setAdditionalServices(servicesAdapter.getSelectedServiceNames());

        return vehicleInfo;
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
        if (getView() != null) {
            populateData();
        }
    }

    public void setVehicleOptions(List<VehicleType> types, List<AdditionalService> services) {
        this.vehicleTypes = types;
        this.availableServices = services;
        if (getView() != null) {
            setupVehicleTypes();
            setupAdditionalServices();
        }
    }
}
