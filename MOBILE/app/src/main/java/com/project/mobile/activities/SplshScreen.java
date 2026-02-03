package com.project.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.window.SplashScreen;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.AdminActivity;
import com.project.mobile.DTO.MeInfo;
import com.project.mobile.DriverActivity;
import com.project.mobile.LoginActivity;
import com.project.mobile.MainActivity;
import com.project.mobile.R;
import com.project.mobile.UnregisterActivity;
import com.project.mobile.viewModels.AuthModel;

public class SplshScreen extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;
    private AuthModel authModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splsh_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authModel = new ViewModelProvider(this).get(AuthModel.class);

        // load user info (cached or fetch from API)
        authModel.getMeInfo().thenAccept(meInfo -> {
            runOnUiThread(() -> {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    System.out.println(meInfo);
                    Log.d("SPLASH_SCREEN", "User role: " + (meInfo != null ? meInfo.getRole() : "null"));
                    redirectUser(meInfo); // redirect based on role
                }, SPLASH_DELAY);
            });
        }).exceptionally(e -> {
            // if failed to fetch MeInfo, go to LoginActivity
            runOnUiThread(() -> {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(this, UnregisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    finish();
                }, SPLASH_DELAY);
            });
            return null;
        });
    }

    // redirect user based on role
    private void redirectUser(MeInfo meInfo) {
        Intent intent;
        Log.d("SPLASH_SCREEN", "Redirecting user with role: " + (meInfo != null ? meInfo.getRole() : "null"));
        if(meInfo == null) {
            intent = new Intent(this, UnregisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        if (meInfo.getRole().equalsIgnoreCase("ADMIN")) {
            intent = new Intent(this, AdminActivity.class);
        } else if (meInfo.getRole().equalsIgnoreCase("DRIVER")) {
            intent = new Intent(this, DriverActivity.class);
        } else if(meInfo.getRole().equals("CUSTOMER")) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, UnregisterActivity.class);

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    }