package com.project.mobile.fragments.reports;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.project.mobile.DTO.reports.AggregatedReportDTO;
import com.project.mobile.DTO.reports.AggregatedUserReportDTO;
import com.project.mobile.DTO.reports.DailyRideStats;
import com.project.mobile.DTO.reports.RideReportDTO;
import com.project.mobile.R;
import com.project.mobile.adapters.UserReportCardAdapter;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.ReportsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReportsFragment extends Fragment {

    // Services
    private ReportsService reportsService;

    // UI Components
    private EditText etStartDate, etEndDate;
    private LinearLayout adminFilters, adminUserSection;
    private Spinner spinnerUserType;
    private Button btnLastWeek, btnLastMonth, btnLastQuarter, btnLastYear;
    private LinearLayout loadingView, contentView;
    private LinearLayout combinedStatsCard;
    private TextView tvCombinedRides, tvCombinedDistance, tvCombinedAmount;
    private TextView tvUsersTitle, tvAmountChartTitle;
    private RecyclerView rvUserReports;

    // Summary stats views
    private View statTotalRides, statTotalDistance, statTotalAmount;
    private View statAvgRidesPerDay, statAvgDistancePerDay, statAvgAmountPerDay;
    private View statAvgDistancePerRide, statAvgAmountPerRide;

    // Charts
    private BarChart chartRides;
    private LineChart chartDistance, chartAmount;

    // Adapter
    private UserReportCardAdapter userReportCardAdapter;

    // Data
    private String startDate;
    private String endDate;
    private String selectedUserType = "DRIVER";
    private Long selectedUserId = null;
    private boolean isAdmin = false;
    private RideReportDTO currentReport;
    private AggregatedReportDTO aggregatedReport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        // Initialize service
        Retrofit retrofit = getRetrofitInstance(); // TODO: Implement this
        reportsService = retrofit.create(ReportsService.class);

        // Check if user is admin
        isAdmin = checkIfUserIsAdmin(); // TODO: Implement this

        // Initialize dates
        initializeDates();

        initViews(view);
        setupDatePickers();
        setupQuickFilters();
        setupAdminFilters();
        setupUserReportsList();
        loadData();

        return view;
    }

    private void initViews(View view) {
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        adminFilters = view.findViewById(R.id.adminFilters);
        spinnerUserType = view.findViewById(R.id.spinnerUserType);
        btnLastWeek = view.findViewById(R.id.btnLastWeek);
        btnLastMonth = view.findViewById(R.id.btnLastMonth);
        btnLastQuarter = view.findViewById(R.id.btnLastQuarter);
        btnLastYear = view.findViewById(R.id.btnLastYear);

        loadingView = view.findViewById(R.id.loadingView);
        contentView = view.findViewById(R.id.contentView);

        adminUserSection = view.findViewById(R.id.adminUserSection);
        combinedStatsCard = view.findViewById(R.id.combinedStatsCard);
        tvCombinedRides = view.findViewById(R.id.tvCombinedRides);
        tvCombinedDistance = view.findViewById(R.id.tvCombinedDistance);
        tvCombinedAmount = view.findViewById(R.id.tvCombinedAmount);
        tvUsersTitle = view.findViewById(R.id.tvUsersTitle);
        rvUserReports = view.findViewById(R.id.rvUserReports);
        tvAmountChartTitle = view.findViewById(R.id.tvAmountChartTitle);

        // Summary stats
        statTotalRides = view.findViewById(R.id.statTotalRides);
        statTotalDistance = view.findViewById(R.id.statTotalDistance);
        statTotalAmount = view.findViewById(R.id.statTotalAmount);
        statAvgRidesPerDay = view.findViewById(R.id.statAvgRidesPerDay);
        statAvgDistancePerDay = view.findViewById(R.id.statAvgDistancePerDay);
        statAvgAmountPerDay = view.findViewById(R.id.statAvgAmountPerDay);
        statAvgDistancePerRide = view.findViewById(R.id.statAvgDistancePerRide);
        statAvgAmountPerRide = view.findViewById(R.id.statAvgAmountPerRide);

        // Charts
        chartRides = view.findViewById(R.id.chartRides);
        chartDistance = view.findViewById(R.id.chartDistance);
        chartAmount = view.findViewById(R.id.chartAmount);

        // Show/hide admin-specific UI
        if (isAdmin) {
            adminFilters.setVisibility(View.VISIBLE);
            adminUserSection.setVisibility(View.VISIBLE);
        } else {
            adminFilters.setVisibility(View.GONE);
            adminUserSection.setVisibility(View.GONE);
        }

        // Set initial dates
        etStartDate.setText(startDate);
        etEndDate.setText(endDate);
    }

    private void initializeDates() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        endDate = sdf.format(cal.getTime());
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        startDate = sdf.format(cal.getTime());
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar cal = Calendar.getInstance();
        String currentDate = isStartDate ? startDate : endDate;
        
        if (currentDate != null && !currentDate.isEmpty()) {
            String[] parts = currentDate.split("-");
            if (parts.length == 3) {
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, 
                    Integer.parseInt(parts[2]));
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", 
                    year, month + 1, dayOfMonth);
                if (isStartDate) {
                    startDate = date;
                    etStartDate.setText(date);
                } else {
                    endDate = date;
                    etEndDate.setText(date);
                }
                loadData();
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupQuickFilters() {
        btnLastWeek.setOnClickListener(v -> setDateRange(7));
        btnLastMonth.setOnClickListener(v -> setDateRange(30));
        btnLastQuarter.setOnClickListener(v -> setDateRange(90));
        btnLastYear.setOnClickListener(v -> setDateRange(365));
    }

    private void setDateRange(int daysBack) {
        Calendar endCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.DAY_OF_YEAR, -daysBack);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDate = sdf.format(startCal.getTime());
        endDate = sdf.format(endCal.getTime());

        etStartDate.setText(startDate);
        etEndDate.setText(endDate);

        loadData();
    }

    private void setupAdminFilters() {
        if (!isAdmin) return;

        List<String> userTypes = new ArrayList<>();
        userTypes.add("DRIVER");
        userTypes.add("PASSENGER");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        spinnerUserType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, 
                                       int position, long id) {
                selectedUserType = userTypes.get(position);
                selectedUserId = null;
                loadData();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupUserReportsList() {
        if (!isAdmin) return;

        userReportCardAdapter = new UserReportCardAdapter();
        rvUserReports.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUserReports.setAdapter(userReportCardAdapter);

        userReportCardAdapter.setOnUserClickListener(this::loadSpecificUserReport);

        combinedStatsCard.setOnClickListener(v -> {
            selectedUserId = null;
            userReportCardAdapter.setSelectedUserId(null);
            if (aggregatedReport != null) {
                currentReport = aggregatedReport.getCombinedStats();
                updateUI();
            }
        });
    }
    // ─── DATA LOADING ──────────────────────────────────────────────────────

    private void loadData() {
        if (startDate == null || endDate == null) return;

        showLoading(true);

        if (isAdmin) {
            loadAggregatedReport();
        } else {
            loadMyReport();
        }
    }

    private void loadMyReport() {
        reportsService.getMyReport(startDate, endDate).enqueue(new Callback<RideReportDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideReportDTO> call, 
                                   @NonNull Response<RideReportDTO> response) {
                if (!isAdded()) return;

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    currentReport = response.body();
                    updateUI();
                } else {
                    Toast.makeText(requireContext(), R.string.error_loading_report, 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideReportDTO> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                Toast.makeText(requireContext(), 
                    getString(R.string.error_loading_report) + " " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAggregatedReport() {
        reportsService.getAggregatedReport(startDate, endDate, selectedUserType)
            .enqueue(new Callback<AggregatedReportDTO>() {
                @Override
                public void onResponse(@NonNull Call<AggregatedReportDTO> call, 
                                       @NonNull Response<AggregatedReportDTO> response) {
                    if (!isAdded()) return;

                    showLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        aggregatedReport = response.body();
                        currentReport = aggregatedReport.getCombinedStats();
                        updateAdminUserSection();
                        updateUI();
                    } else {
                        Toast.makeText(requireContext(), R.string.error_loading_report, 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AggregatedReportDTO> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    showLoading(false);
                    Toast.makeText(requireContext(), 
                        getString(R.string.error_loading_report) + " " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadSpecificUserReport(long userId) {
        showLoading(true);
        selectedUserId = userId;
        userReportCardAdapter.setSelectedUserId(userId);

        reportsService.getUserReport(userId, startDate, endDate)
            .enqueue(new Callback<RideReportDTO>() {
                @Override
                public void onResponse(@NonNull Call<RideReportDTO> call, 
                                       @NonNull Response<RideReportDTO> response) {
                    if (!isAdded()) return;

                    showLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        currentReport = response.body();
                        updateUI();
                    } else {
                        Toast.makeText(requireContext(), R.string.error_loading_report, 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RideReportDTO> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    showLoading(false);
                    Toast.makeText(requireContext(), 
                        getString(R.string.error_loading_report) + " " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    // ─── UI UPDATE ─────────────────────────────────────────────────────────

    private void updateAdminUserSection() {
        if (!isAdmin || aggregatedReport == null) return;

        // Update title
        String title = selectedUserType.equals("DRIVER") ? 
            getString(R.string.section_drivers_report) : 
            getString(R.string.section_passengers_report);
        tvUsersTitle.setText(title);

        // Update combined stats card
        RideReportDTO combined = aggregatedReport.getCombinedStats();
        tvCombinedRides.setText(String.valueOf(combined.getTotalRides()));
        tvCombinedDistance.setText(String.format(Locale.getDefault(), "%.2f km", 
            combined.getTotalDistance()));
        tvCombinedAmount.setText(String.format(Locale.getDefault(), "%.2f RSD", 
            combined.getTotalAmount()));

        // Update user cards
        userReportCardAdapter.setUserReports(aggregatedReport.getUserReports());
        userReportCardAdapter.setSelectedUserId(selectedUserId);
    }

    private void updateUI() {
        if (currentReport == null) return;

        contentView.setVisibility(View.VISIBLE);
        updateSummaryStats();
        renderCharts();
        
        // Update amount chart title based on user type
        if (!isAdmin) {
            String amountLabel = getUserRole().equals("DRIVER") ? 
                getString(R.string.chart_amount_earned_title) :
                getString(R.string.chart_amount_spent_title);
            tvAmountChartTitle.setText(amountLabel);
        }
    }

    private void updateSummaryStats() {
        // Cumulative totals
        setStat(statTotalRides, getString(R.string.stat_total_rides), 
            String.valueOf(currentReport.getTotalRides()));
        setStat(statTotalDistance, getString(R.string.stat_total_distance), 
            String.format(Locale.getDefault(), "%.2f km", currentReport.getTotalDistance()));
        setStat(statTotalAmount, getString(R.string.stat_total_amount), 
            String.format(Locale.getDefault(), "%.2f RSD", currentReport.getTotalAmount()));

        // Daily averages
        setStat(statAvgRidesPerDay, getString(R.string.stat_avg_rides_per_day), 
            String.format(Locale.getDefault(), "%.2f", currentReport.getAverageRidesPerDay()));
        setStat(statAvgDistancePerDay, getString(R.string.stat_avg_distance_per_day), 
            String.format(Locale.getDefault(), "%.2f km", currentReport.getAverageDistancePerDay()));
        setStat(statAvgAmountPerDay, getString(R.string.stat_avg_amount_per_day), 
            String.format(Locale.getDefault(), "%.2f RSD", currentReport.getAverageAmountPerDay()));

        // Per ride averages
        setStat(statAvgDistancePerRide, getString(R.string.stat_avg_distance_per_ride), 
            String.format(Locale.getDefault(), "%.2f km", currentReport.getAverageDistancePerRide()));
        setStat(statAvgAmountPerRide, getString(R.string.stat_avg_amount_per_ride), 
            String.format(Locale.getDefault(), "%.2f RSD", currentReport.getAverageAmountPerRide()));
    }

    private void setStat(View statView, String label, String value) {
        TextView tvLabel = statView.findViewById(R.id.tvStatLabel);
        TextView tvValue = statView.findViewById(R.id.tvStatValue);
        tvLabel.setText(label);
        tvValue.setText(value);
    }
    // ─── CHART RENDERING ───────────────────────────────────────────────────

    private void renderCharts() {
        if (currentReport == null || currentReport.getDailyStats().isEmpty()) return;

        List<DailyRideStats> dailyStats = currentReport.getDailyStats();
        List<String> labels = new ArrayList<>();
        
        for (DailyRideStats stat : dailyStats) {
            labels.add(formatDate(stat.getDate()));
        }

        renderRidesChart(dailyStats, labels);
        renderDistanceChart(dailyStats, labels);
        renderAmountChart(dailyStats, labels);
    }

    private void renderRidesChart(List<DailyRideStats> dailyStats, List<String> labels) {
        List<BarEntry> entries = new ArrayList<>();
        
        for (int i = 0; i < dailyStats.size(); i++) {
            entries.add(new BarEntry(i, dailyStats.get(i).getNumberOfRides()));
        }

        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.chart_rides_label));
        dataSet.setColor(Color.parseColor("#D4AF37")); // Gold
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chartRides.setData(barData);
        configureChart(chartRides, labels);
        chartRides.invalidate();
    }

    private void renderDistanceChart(List<DailyRideStats> dailyStats, List<String> labels) {
        List<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < dailyStats.size(); i++) {
            entries.add(new Entry(i, (float) dailyStats.get(i).getTotalDistance()));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.chart_distance_label));
        dataSet.setColor(Color.parseColor("#D4AF37")); // Gold
        dataSet.setCircleColor(Color.parseColor("#D4AF37"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#80D4AF37")); // Gold with alpha
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);

        chartDistance.setData(lineData);
        configureChart(chartDistance, labels);
        chartDistance.invalidate();
    }

    private void renderAmountChart(List<DailyRideStats> dailyStats, List<String> labels) {
        List<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < dailyStats.size(); i++) {
            entries.add(new Entry(i, (float) dailyStats.get(i).getTotalAmount()));
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.chart_amount_label));
        dataSet.setColor(Color.parseColor("#2E7D32")); // Green
        dataSet.setCircleColor(Color.parseColor("#2E7D32"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#802E7D32")); // Green with alpha
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);

        chartAmount.setData(lineData);
        configureChart(chartAmount, labels);
        chartAmount.invalidate();
    }

    private void configureChart(com.github.mikephil.charting.charts.Chart<?> chart, 
                                List<String> labels) {
        // General settings
        chart.getDescription().setEnabled(false);
        //chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        //chart.setDragEnabled(true);
        //chart.setScaleEnabled(true);
        //chart.setPinchZoom(true);

        // X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // Y-Axis (Left)
        //chart.getAxisLeft().setTextColor(Color.WHITE);
        //chart.getAxisLeft().setDrawGridLines(true);
        //chart.getAxisLeft().setGridColor(Color.parseColor("#33FFFFFF"));

        // Y-Axis (Right) - disable
        //chart.getAxisRight().setEnabled(false);

        // Legend
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setTextSize(12f);
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(dateStr));
        } catch (Exception e) {
            return dateStr;
        }
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            contentView.setVisibility(View.GONE);
        }
    }

    private Retrofit getRetrofitInstance() {
        // return RetrofitClient.retrofit;
        return RetrofitClient.retrofit;
    }

    private boolean checkIfUserIsAdmin() {
        // return currentUserRole.equals("ADMIN");
        // For now, return false. Replace with actual check.
        return false;
    }

    private String getUserRole() {
        // return currentUserRole;
        // For now, return "CUSTOMER". Replace with actual role.
        return "CUSTOMER";
    }
}
