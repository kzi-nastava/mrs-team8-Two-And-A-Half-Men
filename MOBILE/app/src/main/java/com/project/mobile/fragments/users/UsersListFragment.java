package com.project.mobile.fragments.users;

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

import com.project.mobile.DTO.users.User;
import com.project.mobile.DTO.users.UserFilters;
import com.project.mobile.DTO.users.UserPageResponse;
import com.project.mobile.R;
import com.project.mobile.adapters.UsersAdapter;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.AdminUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UsersListFragment extends Fragment {

    // Service
    private AdminUserService adminUserService;

    // UI Components
    private Button btnToggleFilters, btnCreateDriver, btnApplyFilters, btnClearFilters;
    private LinearLayout filtersSection, paginationContainer, loadingView;
    private EditText etFilterEmail, etFilterFirstName, etFilterLastName;
    private Spinner spinnerFilterRole, spinnerFilterDriverStatus, spinnerFilterBlocked, spinnerFilterPending;
    private RecyclerView rvUsers;
    private TextView tvPaginationInfo, tvPageNumber, noUsersView;
    private Button btnFirstPage, btnPrevPage, btnNextPage, btnLastPage;
    private Spinner spinnerPageSize;

    // Adapter
    private UsersAdapter usersAdapter;

    // Data
    private UserFilters filters = new UserFilters();
    private int currentPage = 0;
    private int pageSize = 10;
    private int totalPages = 0;
    private int totalElements = 0;
    private String sortColumn = null;
    private String sortDirection = "ASC";

    // State
    private boolean filtersVisible = false;
    private boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize service
        Retrofit retrofit = getRetrofitInstance(); // TODO: Implement this
        adminUserService = retrofit.create(AdminUserService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupFilterSpinners();
        setupButtons();
        loadUsers();
        
        return view;
    }

    private void initViews(View view) {
        btnToggleFilters = view.findViewById(R.id.btnToggleFilters);
        btnCreateDriver = view.findViewById(R.id.btnCreateDriver);
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnClearFilters = view.findViewById(R.id.btnClearFilters);
        
        filtersSection = view.findViewById(R.id.filtersSection);
        paginationContainer = view.findViewById(R.id.paginationContainer);
        loadingView = view.findViewById(R.id.loadingView);
        
        etFilterEmail = view.findViewById(R.id.etFilterEmail);
        etFilterFirstName = view.findViewById(R.id.etFilterFirstName);
        etFilterLastName = view.findViewById(R.id.etFilterLastName);
        
        spinnerFilterRole = view.findViewById(R.id.spinnerFilterRole);
        spinnerFilterDriverStatus = view.findViewById(R.id.spinnerFilterDriverStatus);
        spinnerFilterBlocked = view.findViewById(R.id.spinnerFilterBlocked);
        spinnerFilterPending = view.findViewById(R.id.spinnerFilterPending);
        
        rvUsers = view.findViewById(R.id.rvUsers);
        tvPaginationInfo = view.findViewById(R.id.tvPaginationInfo);
        tvPageNumber = view.findViewById(R.id.tvPageNumber);
        
        btnFirstPage = view.findViewById(R.id.btnFirstPage);
        btnPrevPage = view.findViewById(R.id.btnPrevPage);
        btnNextPage = view.findViewById(R.id.btnNextPage);
        btnLastPage = view.findViewById(R.id.btnLastPage);
        
        spinnerPageSize = view.findViewById(R.id.spinnerPageSize);
        noUsersView = view.findViewById(R.id.noUserFound);
    }

    private void setupRecyclerView() {
        usersAdapter = new UsersAdapter();
        usersAdapter.setOnUserClickListener(this::onUserClick);
        
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvUsers.setAdapter(usersAdapter);
    }

    private void setupFilterSpinners() {
        // Role filter
        List<String> roles = new ArrayList<>(Arrays.asList(
            getString(R.string.filter_all_roles),
            getString(R.string.role_admin),
            getString(R.string.role_customer),
            getString(R.string.role_driver)
        ));
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, roles
        );
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterRole.setAdapter(roleAdapter);

        // Driver status filter
        List<String> statuses = new ArrayList<>(Arrays.asList(
            getString(R.string.filter_all_statuses),
            getString(R.string.status_busy),
            getString(R.string.status_inactive),
            getString(R.string.status_available)
        ));
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, statuses
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterDriverStatus.setAdapter(statusAdapter);

        // Blocked filter
        List<String> blockedOptions = new ArrayList<>(Arrays.asList(
            getString(R.string.filter_all),
            getString(R.string.filter_blocked),
            getString(R.string.filter_active)
        ));
        ArrayAdapter<String> blockedAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, blockedOptions
        );
        blockedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterBlocked.setAdapter(blockedAdapter);

        // Pending requests filter
        List<String> pendingOptions = new ArrayList<>(Arrays.asList(
            getString(R.string.filter_all),
            getString(R.string.filter_has_pending),
            getString(R.string.filter_no_pending)
        ));
        ArrayAdapter<String> pendingAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, pendingOptions
        );
        pendingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterPending.setAdapter(pendingAdapter);

        // Page size spinner
        List<String> pageSizes = new ArrayList<>(Arrays.asList("5", "10", "20", "50"));
        ArrayAdapter<String> pageSizeAdapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, pageSizes
        );
        pageSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPageSize.setAdapter(pageSizeAdapter);
        spinnerPageSize.setSelection(1); // Default to 10
    }

    private void setupButtons() {
        btnToggleFilters.setOnClickListener(v -> toggleFilters());
        btnCreateDriver.setOnClickListener(v -> createDriver());
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
        
        btnFirstPage.setOnClickListener(v -> changePage(0));
        btnPrevPage.setOnClickListener(v -> changePage(currentPage - 1));
        btnNextPage.setOnClickListener(v -> changePage(currentPage + 1));
        btnLastPage.setOnClickListener(v -> changePage(totalPages - 1));
        
        spinnerPageSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String[] sizes = {"5", "10", "20", "50"};
                pageSize = Integer.parseInt(sizes[position]);
                currentPage = 0;
                loadUsers();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadUsers() {
        if (isLoading) return;
        
        isLoading = true;
        showLoading(true);
        
        adminUserService.getUsers(
            currentPage,
            pageSize,
            sortColumn,
            sortDirection,
            filters.getEmail(),
            filters.getFirstName(),
            filters.getLastName(),
            filters.getRole(),
            filters.getIsBlocked(),
            filters.getDriverStatus(),
            filters.getHasPendingRequests()
        ).enqueue(new Callback<UserPageResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserPageResponse> call, @NonNull Response<UserPageResponse> response) {
                if (!isAdded()) return;
                
                isLoading = false;
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    UserPageResponse pageResponse = response.body();
                    usersAdapter.setUsers(pageResponse.getContent());
                    totalPages = pageResponse.getTotalPages();
                    totalElements = pageResponse.getTotalElements();
                    
                    updateUI();
                } else {
                    Toast.makeText(requireContext(), R.string.error_loading_users, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserPageResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                
                isLoading = false;
                showLoading(false);
                Toast.makeText(requireContext(), 
                    getString(R.string.error_loading_users) + " " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
        paginationContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateUI() {
        // Show/hide pagination
        paginationContainer.setVisibility(totalElements > 0 ? View.VISIBLE : View.GONE);
        noUsersView.setVisibility(totalElements == 0 ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(totalElements > 0 ? View.VISIBLE : View.GONE);
        
        // Update pagination info
        int start = currentPage * pageSize + 1;
        int end = Math.min((currentPage + 1) * pageSize, totalElements);
        tvPaginationInfo.setText(getString(R.string.pagination_showing, start, end, totalElements));
        
        // Update page number
        tvPageNumber.setText(String.valueOf(currentPage + 1));
        
        // Enable/disable buttons
        btnFirstPage.setEnabled(currentPage > 0);
        btnPrevPage.setEnabled(currentPage > 0);
        btnNextPage.setEnabled(currentPage < totalPages - 1);
        btnLastPage.setEnabled(currentPage < totalPages - 1);
    }

    private void toggleFilters() {
        filtersVisible = !filtersVisible;
        filtersSection.setVisibility(filtersVisible ? View.VISIBLE : View.GONE);
        btnToggleFilters.setText(filtersVisible ? R.string.btn_hide_filters : R.string.btn_show_filters);
    }

    private void applyFilters() {
        // Get filter values from UI
        String email = etFilterEmail.getText().toString().trim();
        String firstName = etFilterFirstName.getText().toString().trim();
        String lastName = etFilterLastName.getText().toString().trim();
        
        filters.setEmail(email.isEmpty() ? null : email);
        filters.setFirstName(firstName.isEmpty() ? null : firstName);
        filters.setLastName(lastName.isEmpty() ? null : lastName);
        
        // Role
        int rolePos = spinnerFilterRole.getSelectedItemPosition();
        filters.setRole(rolePos == 0 ? null : (String) spinnerFilterRole.getSelectedItem());
        
        // Driver status
        int statusPos = spinnerFilterDriverStatus.getSelectedItemPosition();
        filters.setDriverStatus(statusPos == 0 ? null : (String) spinnerFilterDriverStatus.getSelectedItem());
        
        // Blocked
        int blockedPos = spinnerFilterBlocked.getSelectedItemPosition();
        filters.setIsBlocked(blockedPos == 0 ? null : blockedPos == 1);
        
        // Pending requests
        int pendingPos = spinnerFilterPending.getSelectedItemPosition();
        filters.setHasPendingRequests(pendingPos == 0 ? null : pendingPos == 1);
        
        currentPage = 0;
        loadUsers();
    }

    private void clearFilters() {
        filters.clear();
        
        etFilterEmail.setText("");
        etFilterFirstName.setText("");
        etFilterLastName.setText("");
        spinnerFilterRole.setSelection(0);
        spinnerFilterDriverStatus.setSelection(0);
        spinnerFilterBlocked.setSelection(0);
        spinnerFilterPending.setSelection(0);
        
        currentPage = 0;
        loadUsers();
    }

    private void changePage(int page) {
        if (page >= 0 && page < totalPages && page != currentPage) {
            currentPage = page;
            loadUsers();
        }
    }

    private void onUserClick(User user) {
        // TODO: Navigate to user details page
        Toast.makeText(requireContext(), "View user: " + user.getEmail(), Toast.LENGTH_SHORT).show();
        
        // Example navigation:
        // Intent intent = new Intent(requireContext(), UserDetailsActivity.class);
        // intent.putExtra("USER_ID", user.getId());
        // startActivity(intent);
    }

    private void createDriver() {
        // TODO: Navigate to create driver page
        Toast.makeText(requireContext(), "Create driver functionality", Toast.LENGTH_SHORT).show();
        
        // Example navigation:
        // Intent intent = new Intent(requireContext(), CreateDriverActivity.class);
        // startActivity(intent);
    }

    // TODO: Implement this method to get your Retrofit instance
    private Retrofit getRetrofitInstance() {
        // Return your Retrofit instance here
        return RetrofitClient.retrofit;
    }
}
