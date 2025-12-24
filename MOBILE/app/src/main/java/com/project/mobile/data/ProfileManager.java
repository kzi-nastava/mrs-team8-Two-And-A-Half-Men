package com.project.mobile.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.project.mobile.models.UserProfile;
import com.project.mobile.models.VehicleInfo;
import com.project.mobile.models.PendingChange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileManager {
    private static ProfileManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private static final String PREFS_NAME = "TaxiAppPrefs";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_VEHICLE_INFO = "vehicle_info";
    private static final String KEY_PENDING_CHANGES = "pending_changes";
    private static final String KEY_HAS_PENDING = "has_pending";

    private ProfileManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized ProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileManager(context.getApplicationContext());
        }
        return instance;
    }

    // User Profile Methods
    public void saveUserProfile(UserProfile profile) {
        String json = gson.toJson(profile);
        sharedPreferences.edit().putString(KEY_USER_PROFILE, json).apply();
    }

    public UserProfile getUserProfile() {
        String json = sharedPreferences.getString(KEY_USER_PROFILE, null);
        if (json != null) {
            return gson.fromJson(json, UserProfile.class);
        }
        // Return default profile
        return new UserProfile("John", "Doe", "+381641234567",
                "Random Street 67, Novi Sad", "example@gmail.com", null);
    }

    // Vehicle Info Methods
    public void saveVehicleInfo(VehicleInfo vehicleInfo) {
        String json = gson.toJson(vehicleInfo);
        sharedPreferences.edit().putString(KEY_VEHICLE_INFO, json).apply();
    }

    public VehicleInfo getVehicleInfo() {
        String json = sharedPreferences.getString(KEY_VEHICLE_INFO, null);
        if (json != null) {
            return gson.fromJson(json, VehicleInfo.class);
        }
        // Return default vehicle info
        List<String> services = Arrays.asList("pet-friendly", "baby-seat", "smoking-allowed");
        return new VehicleInfo("Car", 5, "Fiat Punto", "AB-123-CD", services);
    }

    // Pending Changes Methods
    public void savePendingChanges(List<PendingChange> changes) {
        String json = gson.toJson(changes);
        sharedPreferences.edit()
                .putString(KEY_PENDING_CHANGES, json)
                .putBoolean(KEY_HAS_PENDING, !changes.isEmpty())
                .apply();
    }

    public List<PendingChange> getPendingChanges() {
        String json = sharedPreferences.getString(KEY_PENDING_CHANGES, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<PendingChange>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public boolean hasPendingChanges() {
        return sharedPreferences.getBoolean(KEY_HAS_PENDING, false);
    }

    public void clearPendingChanges() {
        sharedPreferences.edit()
                .remove(KEY_PENDING_CHANGES)
                .putBoolean(KEY_HAS_PENDING, false)
                .apply();
    }

    // Vehicle Types
    public List<String> getVehicleTypes() {
        return Arrays.asList("Car", "Van", "SUV", "Minibus");
    }

    // Additional Services
    public List<String> getAvailableServices() {
        return Arrays.asList(
                "Pet friendly",
                "Baby seat",
                "Smoking allowed",
                "Wheelchair accessible",
                "WiFi"
        );
    }

    public List<String> getAvailableServiceIds() {
        return Arrays.asList(
                "pet-friendly",
                "baby-seat",
                "smoking-allowed",
                "wheelchair-accessible",
                "wifi"
        );
    }

    // Clear all data
    public void clearAllData() {
        sharedPreferences.edit().clear().apply();
    }
}