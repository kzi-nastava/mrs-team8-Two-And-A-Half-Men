package com.project.mobile.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.mobile.AdminActivity;
import com.project.mobile.DriverActivity;
import com.project.mobile.activities.MainActivity;
import com.project.mobile.fragments.RideDetailsFragmentActive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to parse notification additionalData URLs and create navigation intents
 * Supported URLs:
 * - /profile → Opens ProfilePageFragment in main activity
 * - /rides/{id} → Opens RideDetailsFragmentActive with ride ID in main activity
 * Usage:
 * NotificationNavigationHelper.navigateFromNotification(context, additionalData, userRole);
 */
public class NotificationNavigationHelper {

    private static final String TAG = "NotificationNav";

    // Intent extras
    public static final String EXTRA_NAVIGATION_TYPE = "navigation_type";
    public static final String EXTRA_RIDE_ID = "ride_id";

    // Navigation types
    public static final String NAV_PROFILE = "profile";
    public static final String NAV_RIDE = "ride";

    // URL patterns
    private static final Pattern PROFILE_PATTERN = Pattern.compile("^/profile/?$");
    private static final Pattern RIDE_PATTERN = Pattern.compile("^/rides/(\\d+)/?$");

    /**
     * Navigate from NotificationsActivity to the main activity with the specified destination
     *
     * @param context Current context (NotificationsActivity)
     * @param additionalData The URL from notification (e.g., "/profile", "/rides/123")
     * @param userRole The role of the logged-in user ("ADMIN", "DRIVER", "CUSTOMER")
     * @return true if navigation intent was created successfully
     */
    public static boolean navigateFromNotification(Context context, String additionalData, String userRole) {
        if (context == null || additionalData == null || additionalData.isEmpty()) {
            Log.w(TAG, "Cannot navigate: context or additionalData is null/empty");
            return false;
        }

        if (userRole == null || userRole.isEmpty()) {
            Log.w(TAG, "Cannot navigate: userRole is null/empty");
            Toast.makeText(context, "Cannot navigate: user role unknown", Toast.LENGTH_SHORT).show();
            return false;
        }

        String url = additionalData.trim();
        Log.d(TAG, "Navigating to: " + url + " for role: " + userRole);

        // Match /profile
        if (PROFILE_PATTERN.matcher(url).matches()) {
            return navigateToProfile(context, userRole);
        }

        // Match /rides/{id}
        Matcher rideMatcher = RIDE_PATTERN.matcher(url);
        if (rideMatcher.matches()) {
            String rideIdStr = rideMatcher.group(1);
            try {
                assert rideIdStr != null;
                long rideId = Long.parseLong(rideIdStr);
                return navigateToRide(context, rideId, userRole);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid ride ID: " + rideIdStr, e);
                Toast.makeText(context, "Invalid ride ID", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Unknown URL pattern
        Log.w(TAG, "Unknown URL pattern: " + url);
        Toast.makeText(context, "Cannot open: " + url, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Navigate to Profile in the appropriate main activity
     */
    private static boolean navigateToProfile(Context context, String userRole) {
        try {
            Log.d(TAG, "Opening profile for role: " + userRole);

            Intent intent = getMainActivityIntent(context, userRole);
            intent.putExtra(EXTRA_NAVIGATION_TYPE, NAV_PROFILE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to profile", e);
            Toast.makeText(context, "Error opening profile", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Navigate to Ride Details in the appropriate main activity
     */
    private static boolean navigateToRide(Context context, long rideId, String userRole) {
        try {
            Log.d(TAG, "Opening ride " + rideId + " for role: " + userRole);

            Intent intent = getMainActivityIntent(context, userRole);
            intent.putExtra(EXTRA_NAVIGATION_TYPE, NAV_RIDE);
            intent.putExtra(EXTRA_RIDE_ID, rideId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to ride details", e);
            Toast.makeText(context, "Error opening ride details", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Get the appropriate main activity intent based on user role
     */
    private static Intent getMainActivityIntent(Context context, String userRole) {
        Class<?> activityClass;

        switch (userRole.toUpperCase()) {
            case "ADMIN":
                activityClass = AdminActivity.class;
                break;
            case "DRIVER":
                activityClass = DriverActivity.class;
                break;
            case "CUSTOMER":
                activityClass = MainActivity.class;
                break;
            default:
                Log.w(TAG, "Unknown user role: " + userRole + ", defaulting to MainActivity");
                activityClass = MainActivity.class;
                break;
        }

        return new Intent(context, activityClass);
    }

    /**
     * Handle navigation intent in main activity (call this in onCreate/onNewIntent)
     *
     * @param activity The main activity
     * @param intent The intent containing navigation data
     * @param containerId The fragment container ID
     * @return true if navigation was handled
     */
    public static boolean handleNavigationIntent(AppCompatActivity activity, Intent intent, int containerId) {
        if (intent == null || !intent.hasExtra(EXTRA_NAVIGATION_TYPE)) {
            return false;
        }

        String navigationType = intent.getStringExtra(EXTRA_NAVIGATION_TYPE);
        if (navigationType == null) {
            return false;
        }

        Log.d(TAG, "Handling navigation type: " + navigationType);

        switch (navigationType) {
            case NAV_PROFILE:
                return handleProfileNavigation(activity, containerId);

            case NAV_RIDE:
                long rideId = intent.getLongExtra(EXTRA_RIDE_ID, -1);
                if (rideId != -1) {
                    return handleRideNavigation(activity, rideId, containerId);
                }
                return false;

            default:
                Log.w(TAG, "Unknown navigation type: " + navigationType);
                return false;
        }
    }

    /**
     * Handle profile navigation
     */
    private static boolean handleProfileNavigation(AppCompatActivity activity, int containerId) {
        try {
            Log.d(TAG, "Loading ProfilePageFragment");

            // Import your FragmentTransition and ProfilePageFragment
            com.project.mobile.FragmentTransition.to(
                    new com.project.mobile.fragments.profile.ProfilePageFragment(),
                    activity,
                    false,
                    containerId
            );
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile fragment", e);
            return false;
        }
    }

    /**
     * Handle ride navigation
     */
    private static boolean handleRideNavigation(AppCompatActivity activity, long rideId, int containerId) {
        try {
            Log.d(TAG, "Loading RideDetailsFragmentActive with ride ID: " + rideId);

            // Import your FragmentTransition and RideDetailsFragmentActive
            com.project.mobile.FragmentTransition.to(
                    RideDetailsFragmentActive.newInstanceWithId(rideId),
                    activity,
                    false,
                    containerId
            );
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading ride fragment", e);
            return false;
        }
    }

    /**
     * Check if a URL is supported
     */
    public static boolean isSupported(String additionalData) {
        if (additionalData == null || additionalData.isEmpty()) {
            return false;
        }

        String url = additionalData.trim();
        return PROFILE_PATTERN.matcher(url).matches()
                || RIDE_PATTERN.matcher(url).matches();
    }

    /**
     * Get a user-friendly description of what the URL will open
     */
    public static String getDescription(String additionalData) {
        if (additionalData == null || additionalData.isEmpty()) {
            return "Unknown";
        }

        String url = additionalData.trim();

        if (PROFILE_PATTERN.matcher(url).matches()) {
            return "Profile";
        }

        Matcher rideMatcher = RIDE_PATTERN.matcher(url);
        if (rideMatcher.matches()) {
            return "Ride #" + rideMatcher.group(1);
        }

        return "Unknown";
    }
}
