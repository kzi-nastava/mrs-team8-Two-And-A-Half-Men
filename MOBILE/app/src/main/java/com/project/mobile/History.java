package com.project.mobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class History extends Fragment {

    private RecyclerView recyclerView;
    private RideHistoryAdapter adapter;
    private List<RideHistory> rideList;

    private TextInputEditText startDateInput;
    private TextInputEditText endDateInput;
    private RadioGroup sortRadioGroup;
    private Button filterButton;

    private Long startDateMillis = null;
    private Long endDateMillis = null;
    private boolean sortAscending = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupDatePickers();
        setupFilterButton();
        loadRideHistory();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        startDateInput = view.findViewById(R.id.startDateInput);
        endDateInput = view.findViewById(R.id.endDateInput);
        sortRadioGroup = view.findViewById(R.id.sortRadioGroup);
        filterButton = view.findViewById(R.id.filterButton);

        sortRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            sortAscending = (checkedId == R.id.radioAsc);
        });
    }

    private void setupRecyclerView() {
        rideList = new ArrayList<>();
        adapter = new RideHistoryAdapter(rideList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupDatePickers() {
        startDateInput.setOnClickListener(v -> showDatePicker(true));
        endDateInput.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isStartDate ? "Select Start Date" : "Select End Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(selection));

            if (isStartDate) {
                startDateMillis = selection;
                startDateInput.setText(dateString);
            } else {
                endDateMillis = selection;
                endDateInput.setText(dateString);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> applyFilter());
    }

    private void applyFilter() {
        // Ovde bi trebalo pozvati API sa filterima
        // Za sada samo sortiramo postojeće podatke
        loadRideHistory();
    }

    private void loadRideHistory() {
        // Simulacija podataka - zameni sa pravim API pozivom
        rideList.clear();

        RideHistory ride1 = new RideHistory();
        ride1.setUserEmail("user1@gmail.com");
        ride1.setScheduled("10.01.2024 14:20");
        ride1.setStarted("10.01.2024 14:30");
        ride1.setEnded("10.01.2024 15:15");
        ride1.setStatus("COMPLETED");
        ride1.setPrice("2500 RSD");
        ride1.setPassengers(1);
        rideList.add(ride1);

        RideHistory ride2 = new RideHistory();
        ride2.setUserEmail("user2@gmail.com");
        ride2.setScheduled("12.01.2024 08:50");
        ride2.setStarted("12.01.2024 09:00");
        ride2.setEnded("12.01.2024 10:45");
        ride2.setStatus("COMPLETED");
        ride2.setPrice("3200 RSD");
        ride2.setPassengers(2);
        rideList.add(ride2);

        RideHistory ride3 = new RideHistory();
        ride3.setUserEmail("user1@gmail.com");
        ride3.setScheduled("10.01.2024 14:20");
        ride3.setStarted("10.01.2024 14:30");
        ride3.setEnded("10.01.2024 15:15");
        ride3.setStatus("COMPLETED");
        ride3.setPrice("2500 RSD");
        ride3.setPassengers(1);
        rideList.add(ride3);

        adapter.notifyDataSetChanged();
    }
}