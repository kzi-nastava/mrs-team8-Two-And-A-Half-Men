package com.project.mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.project.mobile.DTO.notifications.Notification;
import com.project.mobile.databinding.ActivityDriverBinding;
import com.project.mobile.fragments.HistoryFragment;
import com.project.mobile.fragments.chat.ChatFragment;
import com.project.mobile.fragments.profile.ProfilePageFragment;
import com.project.mobile.fragments.reports.ReportsFragment;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.project.mobile.DTO.Auth.MeInfo;
import com.project.mobile.activities.NotificationsActivity;
import com.project.mobile.helpers.NotificationNavigationHelper;
import com.project.mobile.managers.NotificationManager;
import com.project.mobile.viewModels.AuthModel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;


public class DriverActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityDriverBinding binding;
    private ActionBarDrawerToggle drawerToggle;

    private NotificationManager notificationManager;
    private AuthModel authModel;
    private TextView tvNotificationBadge;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Initialize managers
        notificationManager = NotificationManager.getInstance(this);
        authModel = new ViewModelProvider(this).get(AuthModel.class);

        // Subscribe to notifications
        authModel.getMeInfo().thenAccept(meInfo -> {
            if (meInfo != null) {
                runOnUiThread(() -> {
                    notificationManager.subscribeToNotifications(meInfo.getId());
                    requestNotificationPermission();
                });
            }
        });

        // Listen for notification changes to update badge
        notificationManager.addListener(new NotificationManager.NotificationListener() {
            @Override
            public void onNotificationsChanged(List<Notification> notifications) {
                // Will be called when notifications change
            }

            @Override
            public void onUnreadCountChanged(int count) {
                runOnUiThread(() -> updateNotificationBadge(count));
            }

            @Override
            public void onError(String error) {
                // Handle error if needed
            }
        });

        setupBottomNavBarRegistered();

        int containerId = binding.fragmentContainerViewTag.getId();
        FragmentTransition.to(new ProfilePageFragment(), this, false, containerId);
        handleNotificationNavigation();
    }
    private void setupBottomNavBarRegistered(){
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int containerId = binding.fragmentContainerViewTag.getId();
            int itemId = item.getItemId();
            if(itemId == R.id.nav_home)
            {
                FragmentTransition.to(new ProfilePageFragment(), this, false, containerId);
                return true;
            } else if(itemId == R.id.nav_profile)
            {
                FragmentTransition.to(new ProfilePageFragment(), this, false, containerId);
                return true;
            }
            else if(itemId == R.id.nav_reports)
            {
                FragmentTransition.to(new ReportsFragment(), this, false, containerId);
                return true;
            }
            else if(itemId == R.id.nav_history)
            {
                FragmentTransition.to(new HistoryFragment(), this, false, containerId);
                return true;
            }
            else if(itemId == R.id.nav_live_chat)
            {
                FragmentTransition.to(ChatFragment.newInstanceForUser(), this, false, containerId);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        // Setup notification bell with badge
        MenuItem notificationItem = menu.findItem(R.id.action_notifications);
        View notificationView = notificationItem.getActionView();
        assert notificationView != null;
        tvNotificationBadge = notificationView.findViewById(R.id.tvNotificationBadge);

        notificationView.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Initial badge update
        updateNotificationBadge(notificationManager.getUnreadCount());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationBadge(int count) {
        if (tvNotificationBadge != null) {
            if (count > 0) {
                tvNotificationBadge.setVisibility(View.VISIBLE);
                tvNotificationBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                tvNotificationBadge.setVisibility(View.GONE);
            }
        }
    }
    // Add this method
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Add this callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. You won't see push notifications.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No need to unsubscribe here - NotificationManager handles it
        // Only cleanup on logout
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationNavigation();
    }

    // Add this method:
    private void handleNotificationNavigation() {
        int containerId = binding.fragmentContainerViewTag.getId(); // Or your container ID

        boolean handled = NotificationNavigationHelper.handleNavigationIntent(
                this,
                getIntent(),
                containerId
        );

        if (handled) {
            Log.d("DriverActivity", "Handled notification navigation");
            getIntent().removeExtra(NotificationNavigationHelper.EXTRA_NAVIGATION_TYPE);
            getIntent().removeExtra(NotificationNavigationHelper.EXTRA_RIDE_ID);
        }
    }


}