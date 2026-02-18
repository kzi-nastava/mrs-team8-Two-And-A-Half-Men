package com.project.mobile.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.notifications.Notification;
import com.project.mobile.R;
import com.project.mobile.adapters.NotificationsAdapter;
import com.project.mobile.helpers.NotificationNavigationHelper;
import com.project.mobile.managers.NotificationManager;
import com.project.mobile.viewModels.AuthModel;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity
        implements NotificationManager.NotificationListener {

    private NotificationManager notificationManager;
    private NotificationsAdapter unreadAdapter, readAdapter;
    private AuthModel authModel;
    private String cachedUserRole = null; // Cache the user role

    // UI Components
    private Toolbar toolbar;
    private LinearLayout loadingView, contentView, emptyView;
    private LinearLayout unreadSection, readSection;
    private Button btnMarkAllRead, btnClearRead, btnRefresh;
    private RecyclerView rvUnreadNotifications, rvReadNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationManager = NotificationManager.getInstance(this);
        authModel = new ViewModelProvider(this).get(AuthModel.class);

        initViews();
        setupToolbar();
        setupRecyclerViews();
        setupButtons();

        // Fetch user role asynchronously and cache it
        fetchUserRole();

        // Register as listener
        notificationManager.addListener(this);

        // Refresh notifications
        notificationManager.fetchAllNotifications();
    }

    /**
     * Fetch user role asynchronously and cache it for later use
     */
    private void fetchUserRole() {
        authModel.getMeInfo().thenAccept(meInfo -> {
            if (meInfo != null) {
                runOnUiThread(() -> {
                    cachedUserRole = meInfo.getRole();
                });
            }
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        loadingView = findViewById(R.id.loadingView);
        contentView = findViewById(R.id.contentView);
        emptyView = findViewById(R.id.emptyView);

        unreadSection = findViewById(R.id.unreadSection);
        readSection = findViewById(R.id.readSection);

        findViewById(R.id.tvUnreadTitle);
        findViewById(R.id.tvReadTitle);

        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        btnClearRead = findViewById(R.id.btnClearRead);
        btnRefresh = findViewById(R.id.btnRefresh);

        rvUnreadNotifications = findViewById(R.id.rvUnreadNotifications);
        rvReadNotifications = findViewById(R.id.rvReadNotifications);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerViews() {
        // Unread notifications
        unreadAdapter = new NotificationsAdapter();
        rvUnreadNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvUnreadNotifications.setAdapter(unreadAdapter);
        rvUnreadNotifications.setNestedScrollingEnabled(false);

        unreadAdapter.setOnNotificationClickListener(this::onNotificationClick);
        unreadAdapter.setOnNotificationDeleteListener(this::onNotificationDelete);

        // Read notifications
        readAdapter = new NotificationsAdapter();
        rvReadNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvReadNotifications.setAdapter(readAdapter);
        rvReadNotifications.setNestedScrollingEnabled(false);

        readAdapter.setOnNotificationClickListener(this::onNotificationClick);
        readAdapter.setOnNotificationDeleteListener(this::onNotificationDelete);
    }

    private void setupButtons() {
        btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
        btnClearRead.setOnClickListener(v -> clearRead());
        btnRefresh.setOnClickListener(v -> refresh());
    }

    // ─── NOTIFICATION ACTIONS ──────────────────────────────────────────────

    private void onNotificationClick(Notification notification) {
        // Mark as read
        if (!notification.isRead()) {
            notificationManager.markAsRead(notification.getId());
        }

        // Navigate if additionalData is present
        if (notification.getAdditionalData() != null && !notification.getAdditionalData().isEmpty()) {

            // Check if user role is already cached
            if (cachedUserRole != null) {
                navigateToDestination(notification.getAdditionalData(), cachedUserRole);
            } else {
                // User role not cached yet, fetch it now
                authModel.getMeInfo().thenAccept(meInfo -> {
                    if (meInfo != null) {
                        runOnUiThread(() -> {
                            cachedUserRole = meInfo.getRole();
                            navigateToDestination(notification.getAdditionalData(), cachedUserRole);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Cannot navigate: user info not available",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                }).exceptionally(throwable -> {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                    });
                    return null;
                });
            }
        }
    }

    /**
     * Navigate to the destination specified in additionalData
     */
    private void navigateToDestination(String additionalData, String userRole) {
        boolean success = NotificationNavigationHelper.navigateFromNotification(
                this,
                additionalData,
                userRole
        );

        if (success) {
            finish(); // Close NotificationsActivity
        }
    }

    private void onNotificationDelete(Notification notification) {
        notificationManager.deleteNotification(notification.getId());
    }

    private void markAllAsRead() {
        notificationManager.markAllAsRead();
    }

    private void clearRead() {
        notificationManager.clearReadNotifications();
    }

    private void refresh() {
        notificationManager.fetchAllNotifications();
    }

    // ─── NOTIFICATION LISTENER ─────────────────────────────────────────────

    @Override
    public void onNotificationsChanged(List<Notification> notifications) {
        runOnUiThread(() -> {
            List<Notification> unread = notificationManager.getUnreadNotifications();
            List<Notification> read = notificationManager.getReadNotifications();

            // Update adapters
            unreadAdapter.setNotifications(unread);
            readAdapter.setNotifications(read);

            // Update UI visibility
            if (notifications.isEmpty()) {
                contentView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            } else {
                contentView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                loadingView.setVisibility(View.GONE);

                // Show/hide sections
                if (unread.isEmpty()) {
                    unreadSection.setVisibility(View.GONE);
                    btnMarkAllRead.setVisibility(View.GONE);
                } else {
                    unreadSection.setVisibility(View.VISIBLE);
                    btnMarkAllRead.setVisibility(View.VISIBLE);
                }

                if (read.isEmpty()) {
                    readSection.setVisibility(View.GONE);
                } else {
                    readSection.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onUnreadCountChanged(int count) {
        // Update badge if needed
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            loadingView.setVisibility(View.GONE);
        });
    }

    // ─── LIFECYCLE ─────────────────────────────────────────────────────────

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.removeListener(this);
    }
}