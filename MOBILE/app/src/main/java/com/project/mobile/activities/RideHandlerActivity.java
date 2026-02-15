package com.project.mobile.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.project.mobile.fragments.RideDetailsFragmentActive;
import com.project.mobile.R;

public class RideHandlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_handler); // A simple layout with a FrameLayout

        handleIntent();
    }

    private void handleIntent() {
        Uri data = getIntent().getData();
        if (data != null) {
            // URL: http://localhost:4200/rides/52?accessToken=1150...

            // 1. Parse Ride ID (The last path segment: "52")
            String rideIdStr = data.getLastPathSegment();
            Long rideId = null;
            try {
                rideId = Long.parseLong(rideIdStr);
            } catch (NumberFormatException e) {
                Log.e("DeepLink", "Invalid Ride ID");
            }

            // 2. Parse Access Token from Query Parameters
            String accessToken = data.getQueryParameter("accessToken");

            // 3. Create Fragment and load into FrameLayout
            if (rideId != null && accessToken != null) {
                loadFragment(accessToken, rideId);
            }
        }
    }

    private void loadFragment(String token, Long id) {
        RideDetailsFragmentActive fragment = RideDetailsFragmentActive.newInstanceWithAccessToken(token, id);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}