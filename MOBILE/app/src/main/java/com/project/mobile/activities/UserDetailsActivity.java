package com.project.mobile.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.mobile.DTO.profile.PendingChangeRequest;
import com.project.mobile.DTO.profile.PersonalInfo;
import com.project.mobile.DTO.profile.VehicleInfo;
import com.project.mobile.DTO.users.BlockUserRequest;
import com.project.mobile.DTO.users.UserDetailResponse;
import com.project.mobile.DTO.vehicles.AdditionalService;
import com.project.mobile.DTO.vehicles.VehicleOptions;
import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;
import com.project.mobile.adapters.PendingChangesAdapter;
import com.project.mobile.adapters.TabsPagerAdapter;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.fragments.shared.forms.PersonalInfoFormFragment;
import com.project.mobile.fragments.shared.forms.VehicleInfoFormFragment;
import com.project.mobile.helpers.DialogHelper;
import com.project.mobile.helpers.ImageUrlHelper;
import com.project.mobile.service.AdminUserService;
import com.project.mobile.service.VehicleService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "USER_ID";

    // Services
    private AdminUserService adminUserService;
    private VehicleService vehicleService;

    // UI
    private Toolbar toolbar;
    private LinearLayout loadingView, contentView;
    private LinearLayout blockedBanner, pendingBanner;
    private TextView tvBlockReason;
    private Button btnBlock, btnUnblock, btnChat;
    private Button btnApproveChanges, btnRejectChanges;
    private RecyclerView rvPendingChanges;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // Fragments
    private PersonalInfoFormFragment personalInfoFragment;
    private VehicleInfoFormFragment vehicleInfoFragment;
    private TabsPagerAdapter pagerAdapter;

    // Adapter
    private PendingChangesAdapter pendingChangesAdapter;

    // Data
    private long userId;
    private PersonalInfo personalInfo;
    private VehicleInfo vehicleInfo;
    private PendingChangeRequest pendingChangeRequest;
    private List<VehicleType> vehicleTypes = new ArrayList<>();
    private List<AdditionalService> availableServices = new ArrayList<>();

    // State
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userId = getIntent().getLongExtra(EXTRA_USER_ID, -1);
        if (userId == -1) {
            Toast.makeText(this, R.string.error_invalid_user_id, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit retrofit = getRetrofitInstance();
        adminUserService = retrofit.create(AdminUserService.class);
        vehicleService = retrofit.create(VehicleService.class);

        initViews();
        setupToolbar();
        setupPendingChangesRecyclerView();
        setupButtons();
        loadUserDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        loadingView = findViewById(R.id.loadingView);
        contentView = findViewById(R.id.contentView);
        blockedBanner = findViewById(R.id.blockedBanner);
        pendingBanner = findViewById(R.id.pendingBanner);
        tvBlockReason = findViewById(R.id.tvBlockReason);
        btnBlock = findViewById(R.id.btnBlock);
        btnUnblock = findViewById(R.id.btnUnblock);
        btnChat = findViewById(R.id.btnChat);
        btnApproveChanges = findViewById(R.id.btnApproveChanges);
        btnRejectChanges = findViewById(R.id.btnRejectChanges);
        rvPendingChanges = findViewById(R.id.rvPendingChanges);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.user_details_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupPendingChangesRecyclerView() {
        pendingChangesAdapter = new PendingChangesAdapter();
        rvPendingChanges.setLayoutManager(new LinearLayoutManager(this));
        rvPendingChanges.setAdapter(pendingChangesAdapter);
    }

    private void setupButtons() {
        btnChat.setOnClickListener(v -> openChat());
        btnBlock.setOnClickListener(v -> showBlockDialog());
        btnUnblock.setOnClickListener(v -> confirmUnblock());
        btnApproveChanges.setOnClickListener(v -> confirmApproveChanges());
        btnRejectChanges.setOnClickListener(v -> confirmRejectChanges());
    }

    private void loadUserDetails() {
        showLoading(true);

        adminUserService.getUserDetails(userId).enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserDetailResponse> call,
                                   @NonNull Response<UserDetailResponse> response) {
                if (isFinishing()) return;

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(UserDetailsActivity.this,
                        R.string.error_loading_user_details, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetailResponse> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                showLoading(false);
                Toast.makeText(UserDetailsActivity.this,
                    getString(R.string.error_loading_user_details) + " " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI(UserDetailResponse response) {
        personalInfo = response.getPersonalInfo();
        vehicleInfo = response.getVehicleInfo();
        pendingChangeRequest = response.getPendingChangeRequest();

        setupBlockedBanner();
        setupPendingBanner();
        setupBlockUnblockButtons();
        setupTabs();

        if (vehicleInfo != null) {
            loadVehicleOptions();
        }

        contentView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void setupBlockedBanner() {
        if (personalInfo.isBlocked() && personalInfo.getBlockReason() != null) {
            blockedBanner.setVisibility(View.VISIBLE);
            tvBlockReason.setText(getString(R.string.label_block_reason) + " " + personalInfo.getBlockReason());
        } else {
            blockedBanner.setVisibility(View.GONE);
        }
    }

    private void setupPendingBanner() {
        if (pendingChangeRequest != null) {
            pendingBanner.setVisibility(View.VISIBLE);
            List<PendingChangesAdapter.ChangeItem> changes = buildChangesList();
            pendingChangesAdapter.setChanges(changes);
        } else {
            pendingBanner.setVisibility(View.GONE);
        }
    }

    private void setupBlockUnblockButtons() {
        if (personalInfo.isBlocked()) {
            btnBlock.setVisibility(View.GONE);
            btnUnblock.setVisibility(View.VISIBLE);
        } else {
            btnBlock.setVisibility(View.VISIBLE);
            btnUnblock.setVisibility(View.GONE);
        }
    }

    private void setupTabs() {
        // Remove old adapter/fragments if reloading
        if (pagerAdapter != null) {
            pagerAdapter.clearFragments();
        }

        pagerAdapter = new TabsPagerAdapter(this);

        // Personal info - readonly, with photo
        personalInfoFragment = PersonalInfoFormFragment.newInstance(personalInfo, true, true);
        pagerAdapter.addFragment(personalInfoFragment);

        // Vehicle tab - only for drivers
        if (vehicleInfo != null) {
            vehicleInfoFragment = VehicleInfoFormFragment.newInstance(
                vehicleInfo, vehicleTypes, availableServices, true
            );
            pagerAdapter.addFragment(vehicleInfoFragment);
        }

        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? R.string.tab_personal_data : R.string.tab_vehicle_data);
        }).attach();
    }

    private List<PendingChangesAdapter.ChangeItem> buildChangesList() {
        List<PendingChangesAdapter.ChangeItem> changes = new ArrayList<>();
        if (pendingChangeRequest == null) return changes;

        addChangeIfDifferent(changes, getString(R.string.change_field_first_name),
            personalInfo.getFirstName(), pendingChangeRequest.getFirstName());
        addChangeIfDifferent(changes, getString(R.string.change_field_last_name),
            personalInfo.getLastName(), pendingChangeRequest.getLastName());
        addChangeIfDifferent(changes, getString(R.string.change_field_phone),
            personalInfo.getPhoneNumber(), pendingChangeRequest.getPhoneNumber());
        addChangeIfDifferent(changes, getString(R.string.change_field_address),
            personalInfo.getAddress(), pendingChangeRequest.getAddress());
        addChangeIfDifferent(changes, getString(R.string.change_field_email),
            personalInfo.getEmail(), pendingChangeRequest.getEmail());

        if (vehicleInfo != null) {
            addChangeIfDifferent(changes, getString(R.string.change_field_vehicle_type),
                vehicleInfo.getType(), pendingChangeRequest.getVehicleType());
            addChangeIfDifferent(changes, getString(R.string.change_field_model),
                vehicleInfo.getModel(), pendingChangeRequest.getModel());
            addChangeIfDifferent(changes, getString(R.string.change_field_license_plate),
                vehicleInfo.getLicensePlate(), pendingChangeRequest.getLicensePlate());
            addChangeIfDifferent(changes, getString(R.string.change_field_seats),
                String.valueOf(vehicleInfo.getNumberOfSeats()),
                String.valueOf(pendingChangeRequest.getNumberOfSeats()));
        }

        // Image change
        if (pendingChangeRequest.getImgSrc() != null &&
            !pendingChangeRequest.getImgSrc().equals(personalInfo.getImgSrc())) {
            changes.add(new PendingChangesAdapter.ChangeItem(
                getString(R.string.change_field_profile_image),
                ImageUrlHelper.getFullImageUrl(personalInfo.getImgSrc()),
                ImageUrlHelper.getFullImageUrl(pendingChangeRequest.getImgSrc()),
                true
            ));
        }

        return changes;
    }

    private void addChangeIfDifferent(List<PendingChangesAdapter.ChangeItem> changes,
                                      String field, String oldValue, String newValue) {
        if (newValue != null && !newValue.equals(oldValue)) {
            changes.add(new PendingChangesAdapter.ChangeItem(field, oldValue, newValue, false));
        }
    }

    private void loadVehicleOptions() {
        vehicleService.getVehicleOptions().enqueue(new Callback<VehicleOptions>() {
            @Override
            public void onResponse(@NonNull Call<VehicleOptions> call,
                                   @NonNull Response<VehicleOptions> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    vehicleTypes = response.body().getVehicleTypes();
                    availableServices = response.body().getAdditionalServices();
                    if (vehicleInfoFragment != null) {
                        vehicleInfoFragment.setVehicleOptions(vehicleTypes, availableServices);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VehicleOptions> call, @NonNull Throwable t) {
                // Non-critical, vehicle options just won't populate the spinner correctly
            }
        });
    }

    // ─── Block / Unblock ────────────────────────────────────────────

    private void showBlockDialog() {
        // Custom dialog with an EditText for the block reason
        EditText etReason = new EditText(this);
        etReason.setHint(getString(R.string.hint_block_reason));
        etReason.setTextColor(getColor(R.color.primary_text));
        etReason.setPadding(32, 16, 32, 16);

        new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_block_title)
            .setMessage(R.string.dialog_block_message)
            .setView(etReason)
            .setPositiveButton(R.string.btn_block_user, (dialog, which) -> {
                String reason = etReason.getText().toString().trim();
                if (reason.isEmpty()) {
                    Toast.makeText(this, R.string.error_block_reason_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                blockUser(reason);
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private void blockUser(String reason) {
        if (isProcessing) return;
        isProcessing = true;
        setActionButtonsEnabled(false);

        adminUserService.blockUser(userId, new BlockUserRequest(reason))
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (isFinishing()) return;
                    isProcessing = false;
                    setActionButtonsEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDetailsActivity.this,
                            R.string.success_user_blocked, Toast.LENGTH_SHORT).show();
                        loadUserDetails(); // Reload to reflect new state
                    } else {
                        Toast.makeText(UserDetailsActivity.this,
                            R.string.error_blocking_user, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    if (isFinishing()) return;
                    isProcessing = false;
                    setActionButtonsEnabled(true);
                    Toast.makeText(UserDetailsActivity.this,
                        R.string.error_blocking_user, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void confirmUnblock() {
        DialogHelper.showConfirmDialog(this,
            getString(R.string.dialog_unblock_title),
            getString(R.string.dialog_unblock_message),
            this::unblockUser
        );
    }

    private void unblockUser() {
        if (isProcessing) return;
        isProcessing = true;
        setActionButtonsEnabled(false);

        adminUserService.unblockUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (isFinishing()) return;
                isProcessing = false;
                setActionButtonsEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(UserDetailsActivity.this,
                        R.string.success_user_unblocked, Toast.LENGTH_SHORT).show();
                    loadUserDetails();
                } else {
                    Toast.makeText(UserDetailsActivity.this,
                        R.string.error_unblocking_user, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (isFinishing()) return;
                isProcessing = false;
                setActionButtonsEnabled(true);
                Toast.makeText(UserDetailsActivity.this,
                    R.string.error_unblocking_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Pending Changes ────────────────────────────────────────────

    private void confirmApproveChanges() {
        DialogHelper.showConfirmDialog(this,
            getString(R.string.dialog_approve_title),
            getString(R.string.dialog_approve_message),
            this::approveChanges
        );
    }

    private void approveChanges() {
        if (isProcessing || pendingChangeRequest == null) return;
        isProcessing = true;
        setActionButtonsEnabled(false);

        adminUserService.approveChangeRequest(pendingChangeRequest.getId())
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (isFinishing()) return;
                    isProcessing = false;
                    setActionButtonsEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDetailsActivity.this,
                            R.string.success_changes_approved, Toast.LENGTH_SHORT).show();
                        loadUserDetails();
                    } else {
                        Toast.makeText(UserDetailsActivity.this,
                            R.string.error_approving_changes, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    if (isFinishing()) return;
                    isProcessing = false;
                    setActionButtonsEnabled(true);
                    Toast.makeText(UserDetailsActivity.this,
                        R.string.error_approving_changes, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void confirmRejectChanges() {
        DialogHelper.showConfirmDialog(this,
            getString(R.string.dialog_reject_title),
            getString(R.string.dialog_reject_message),
            this::rejectChanges
        );
    }

    private void rejectChanges() {
        if (isProcessing || pendingChangeRequest == null) return;
        isProcessing = true;
        setActionButtonsEnabled(false);

        adminUserService.rejectChangeRequest(pendingChangeRequest.getId())
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (isFinishing()) return;
                    isProcessing = false;
                    setActionButtonsEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDetailsActivity.this,
                            R.string.success_changes_rejected, Toast.LENGTH_SHORT).show();
                        loadUserDetails();
                    } else {
                        Toast.makeText(UserDetailsActivity.this,
                            R.string.error_rejecting_changes, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    if (isFinishing()) return;
                    isProcessing = false;
                    setActionButtonsEnabled(true);
                    Toast.makeText(UserDetailsActivity.this,
                        R.string.error_rejecting_changes, Toast.LENGTH_SHORT).show();
                }
            });
    }

    // ─── Chat ───────────────────────────────────────────────────────

    private void openChat() {
        Toast.makeText(this, "Chat coming soon!", Toast.LENGTH_SHORT).show();
    }

    // ─── Helpers ────────────────────────────────────────────────────

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) contentView.setVisibility(View.GONE);
    }

    private void setActionButtonsEnabled(boolean enabled) {
        btnBlock.setEnabled(enabled);
        btnUnblock.setEnabled(enabled);
        btnApproveChanges.setEnabled(enabled);
        btnRejectChanges.setEnabled(enabled);
    }

    private Retrofit getRetrofitInstance() {
        // return RetrofitClient.retrofit;
        return RetrofitClient.retrofit;
    }
}
