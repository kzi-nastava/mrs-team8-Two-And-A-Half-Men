package com.project.mobile.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.project.mobile.R;
import com.project.mobile.RideHistoryAdapter;
import com.project.mobile.databinding.FragmentHistoryBinding;
import com.project.mobile.service.RideService;
import com.project.mobile.config.HistoryConfig;
import com.project.mobile.core.retrofitClient.RetrofitClient;
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

public class DriverHistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;

    private RideHistoryAdapter adapter;
    private List<Ride> rideList;
    private final RideService rideService = RetrofitClient.retrofit.create(RideService.class);

    // Pagination
    private int currentPage = 0;
    private int pageSize = HistoryConfig.DEFAULT_PAGE_SIZE;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    // Filtering
    private Long startDateMillis = null;
    private Long endDateMillis = null;
    private boolean filtersVisible = false;

    // Sorting
    private HistoryConfig.SortField currentSortField = HistoryConfig.SortField.SCHEDULED_TIME;
    private HistoryConfig.SortDirection currentSortDirection = HistoryConfig.SortDirection.DESC;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupSortSpinners();
        setupPageSizeSpinner();
        setupDatePickers();
        setupFilterToggle();

        loadRideHistory(true);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        rideList = new ArrayList<>();
        adapter = new RideHistoryAdapter(rideList, this::onRideClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

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

    private void setupSortSpinners() {
        // Sort Field
        List<String> sortFieldNames = new ArrayList<>();
        for (HistoryConfig.SortField field : HistoryConfig.SortField.values()) {
            sortFieldNames.add(field.getDisplayName());
        }

        ArrayAdapter<String> sortFieldAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortFieldNames
        );
        sortFieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sortFieldSpinner.setAdapter(sortFieldAdapter);
        binding.sortFieldSpinner.setSelection(0);

        binding.sortFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortField = HistoryConfig.SortField.values()[position];
                resetAndReload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Sort Direction
        List<String> sortDirectionNames = new ArrayList<>();
        for (HistoryConfig.SortDirection direction : HistoryConfig.SortDirection.values()) {
            sortDirectionNames.add(direction.getDisplayName());
        }

        ArrayAdapter<String> sortDirectionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortDirectionNames
        );
        sortDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sortDirectionSpinner.setAdapter(sortDirectionAdapter);
        binding.sortDirectionSpinner.setSelection(1); // DESC by default

        binding.sortDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortDirection = HistoryConfig.SortDirection.values()[position];
                resetAndReload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPageSizeSpinner() {
        List<String> pageSizeOptions = new ArrayList<>();
        for (int size : HistoryConfig.PAGE_SIZE_OPTIONS) {
            pageSizeOptions.add(size + " per page");
        }

        ArrayAdapter<String> pageSizeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                pageSizeOptions
        );
        pageSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.pageSizeSpinner.setAdapter(pageSizeAdapter);
        binding.pageSizeSpinner.setSelection(1); // 10 by default

        binding.pageSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pageSize = HistoryConfig.PAGE_SIZE_OPTIONS[position];
                resetAndReload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDatePickers() {
        binding.startDateInput.setOnClickListener(v -> showDatePicker(true));
        binding.endDateInput.setOnClickListener(v -> showDatePicker(false));

        binding.clearDatesButton.setOnClickListener(v -> {
            startDateMillis = null;
            endDateMillis = null;
            binding.startDateInput.setText("");
            binding.endDateInput.setText("");
            resetAndReload();
        });

        binding.applyFiltersButton.setOnClickListener(v -> resetAndReload());
    }

    private void setupFilterToggle() {
        binding.toggleFiltersButton.setOnClickListener(v -> {
            filtersVisible = !filtersVisible;
            binding.filtersPanel.setVisibility(filtersVisible ? View.VISIBLE : View.GONE);
            binding.toggleFiltersButton.setText(filtersVisible ? "Hide Filters" : "Show Filters");
        });
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
                binding.startDateInput.setText(dateString);
            } else {
                endDateMillis = selection;
                binding.endDateInput.setText(dateString);
            }
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void loadRideHistory(boolean reset) {
        if (isLoading) return;

        if (reset) {
            currentPage = 0;
            isLastPage = false;
            rideList.clear();
            adapter.notifyDataSetChanged();
        }

        isLoading = true;
        showLoading(true);

        String startDate = formatDateForApi(startDateMillis);
        String endDate = formatDateForApi(endDateMillis);
        String sort = currentSortField.getApiValue() + "," + currentSortDirection.getApiValue();

        Call<PagedResponse<Ride>> call = rideService.getRideHistory(
                currentPage,
                pageSize,
                startDate,
                endDate,
                sort
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull Call<PagedResponse<Ride>> call,
                    @NonNull Response<PagedResponse<Ride>> response
            ) {
                isLoading = false;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<Ride> pagedResponse = response.body();
                    List<Ride> rides = pagedResponse.getContent();

                    if (rides != null && !rides.isEmpty()) {
                        rideList.addAll(rides);
                        adapter.notifyDataSetChanged();

                        isLastPage = pagedResponse.isLast();
                        currentPage++;

                        updatePaginationInfo(pagedResponse);
                    } else {
                        if (currentPage == 0) {
                            binding.emptyStateText.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PagedResponse<Ride>> call, @NonNull Throwable t) {
                isLoading = false;
                showLoading(false);
                Log.e(TAG, "Network error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadMoreRides() {
        if (!isLastPage && !isLoading) {
            loadRideHistory(false);
        }
    }

    private void resetAndReload() {
        loadRideHistory(true);
    }

    private void onRideClick(Ride ride) {
        RideDetailsFragmentActive detailsFragment = RideDetailsFragmentActive.newInstanceWithId(ride.getId());

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view_tag, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updatePaginationInfo(PagedResponse<Ride> response) {
        String info = String.format(Locale.getDefault(),
                "Page %d of %d (%d total rides)",
                response.getNumber() + 1,
                response.getTotalPages(),
                response.getTotalElements()
        );
        binding.paginationInfo.setText(info);
        binding.paginationInfo.setVisibility(View.VISIBLE);
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

    private void handleError(int code) {
        String errorMsg = "Failed to load ride history";
        if (code == 401) {
            errorMsg = "Unauthorized - please login again";
        } else if (code == 403) {
            errorMsg = "Access forbidden";
        } else if (code >= 500) {
            errorMsg = "Server error - please try again later";
        }
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.emptyStateText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}