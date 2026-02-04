package com.project.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.R;
import com.project.mobile.viewModels.AuthModel;

public class CustomerAccountActivation extends AppCompatActivity {

    private AuthModel authModel;
    private TextView statusTextView;
    private ProgressBar progressBar;
    private TextView loginLink;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_account_activation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Account Activation");
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);

        authModel = new ViewModelProvider(this).get(AuthModel.class);

        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();

        if (data != null) {
            String token = data.getQueryParameter("token");

            if (token != null && !token.isEmpty()) {
                // Show loading state
                showLoading();
                authModel.activateAccount(token).thenAccept(response -> {
                    runOnUiThread(() -> {
                        hideLoading();
                        showMessage(response.getMessage());

                        if (response.isSuccess()) {
                            loginLink.setVisibility(View.VISIBLE);
                        }
                    });
                });
            } else {
                showMessage("Invalid activation link");
            }
        } else {
            showMessage("No activation link found");
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText("Activating your account...");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showMessage(String message) {
        statusTextView.setText(message);
    }
}