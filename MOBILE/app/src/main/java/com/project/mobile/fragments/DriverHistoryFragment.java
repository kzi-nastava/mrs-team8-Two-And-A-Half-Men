package com.project.mobile.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.project.mobile.R;
import com.project.mobile.RideHistoryAdapter;
import com.project.mobile.api.RideApiService;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.databinding.FragmentHistoryBinding;
import com.project.mobile.models.PagedResponse;
import com.project.mobile.models.Ride;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class DriverHistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;

    private RideHistoryAdapter adapter;
    private List<Ride> rideList;

    private RideApiService apiService;

    private Long startDateMillis = null;
    private Long endDateMillis = null;
    private boolean sortAscending = true;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        apiService = RetrofitClient.getRideApiService();

        setupRecyclerView();
        setupDatePickers();
        setupSortOptions();
        setupFilterButton();
        loadRideHistory();

        return view;
    }

    private void setupRecyclerView() {
        rideList = new ArrayList<>();
        adapter = new RideHistoryAdapter(rideList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        // Add scroll listener for pagination
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreRides();
                    }
                }
            }
        });
    }

    private void setupDatePickers() {
        binding.startDateInput.setOnClickListener(v -> showDatePicker(true));
        binding.endDateInput.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupSortOptions() {
        binding.sortRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            sortAscending = (checkedId == R.id.radioAsc);
        });
    }

    private void showDatePicker(boolean isStartDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isStartDate ? "Start Date" : "End Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(selection));

            if (isStartDate) {
                startDateMillis = selection;
                binding.startDateInput.setText(dateString);
            } else {
                endDateMillis = selection;
                binding.endDateInput.setText(dateString);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void setupFilterButton() {
        binding.filterButton.setOnClickListener(v -> {
            currentPage = 0;
            isLastPage = false;
            rideList.clear();
            adapter.notifyDataSetChanged();
            loadRideHistory();
        });
    }

    private void loadRideHistory() {
        if (isLoading) return;

        isLoading = true;
        showLoading(true);

        String startDate = formatDateForApi(startDateMillis);
        String endDate = formatDateForApi(endDateMillis);
        String sort = sortAscending ? "scheduledTime,asc" : "scheduledTime,desc";

        Call<PagedResponse<Ride>> call = apiService.getRideHistory(
                currentPage,
                PAGE_SIZE,
                startDate,
                endDate,
                sort
        );

        call.enqueue(new Callback<PagedResponse<Ride>>() {
            @Override
            public void onResponse(Call<PagedResponse<Ride>> call, Response<PagedResponse<Ride>> response) {
                isLoading = false;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<Ride> pagedResponse = response.body();
                    List<Ride> rides = pagedResponse.getContent();

                    if (rides != null && !rides.isEmpty()) {
                        for (Ride ride : rides) {
                            rideList.add(ride);
                        }
                        adapter.notifyDataSetChanged();

                        isLastPage = pagedResponse.isLast();
                        currentPage++;
                    } else {
                        if (currentPage == 0) {
                            Toast.makeText(getContext(), "No ride history found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.code() + " - " + response.message());
                    String errorMsg = "Failed to load ride history";
                    if (response.code() == 401) {
                        errorMsg = "Unauthorized - please login again";
                    } else if (response.code() == 403) {
                        errorMsg = "Access forbidden";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error - please try again later";
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Ride>> call, Throwable t) {
                isLoading = false;
                showLoading(false);
                Log.e(TAG, "Network error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadMoreRides() {
        if (!isLastPage && !isLoading) {
            loadRideHistory();
        }
    }

    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return "N/A";
        }

        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, inputFormatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault());
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + isoDateTime, e);
            return isoDateTime;
        }
    }

    private String formatDateForApi(Long milliseconds) {
        if (milliseconds == null) {
            return null;
        }

        try {
            LocalDateTime dateTime = Instant.ofEpochMilli(milliseconds)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0);

            return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date for API", e);
            return null;
        }
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}