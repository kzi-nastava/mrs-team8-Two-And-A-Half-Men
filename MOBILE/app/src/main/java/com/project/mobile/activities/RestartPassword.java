package com.project.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.R;
import com.project.mobile.viewModels.AuthModel;

public class RestartPassword extends AppCompatActivity {

    private AuthModel authModel;
    private TextView statusTextView;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button resetButton;
    private ProgressBar progressBar;
    private TextView loginLink;
    private Toolbar toolbar;
    private String resetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restart_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        statusTextView = findViewById(R.id.statusTextView);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        resetButton = findViewById(R.id.resetButton);
        progressBar = findViewById(R.id.progressBar);
        loginLink = findViewById(R.id.loginLink);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Reset Password");
        }

        // Initialize ViewModel
        authModel = new ViewModelProvider(this).get(AuthModel.class);

        // Setup login link
        loginLink.setOnClickListener(v -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });

        // Setup reset button - THIS IS WHERE THE REQUEST IS SENT
        resetButton.setOnClickListener(v -> handlePasswordReset());

        // Handle deep link - only validate token presence
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
            resetToken = data.getQueryParameter("token");

            if (resetToken != null && !resetToken.isEmpty()) {
                showResetForm();
            } else {
                showError("Invalid reset link");
            }
        } else {
            showError("No reset link found");
        }
    }

    private void handlePasswordReset() {
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (newPassword.isEmpty()) {
            newPasswordInput.setError("Password is required");
            newPasswordInput.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordInput.setError("Password must be at least 6 characters");
            newPasswordInput.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }

        showLoading();

        authModel.resetPassword(resetToken, newPassword).thenAccept(response -> {
            runOnUiThread(() -> {
                hideLoading();

                if (response.isSuccess()) {
                    showSuccess(response.getMessage());
                } else {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showResetForm() {
        statusTextView.setText(R.string.enter_your_new_password);
        statusTextView.setVisibility(View.VISIBLE);
        newPasswordInput.setVisibility(View.VISIBLE);
        confirmPasswordInput.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        resetButton.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        resetButton.setEnabled(true);
    }

    private void showError(String message) {
        statusTextView.setText(message);
        statusTextView.setVisibility(View.VISIBLE);
        newPasswordInput.setVisibility(View.GONE);
        confirmPasswordInput.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
    }

    private void showSuccess(String message) {
        statusTextView.setText(message);
        statusTextView.setVisibility(View.VISIBLE);
        newPasswordInput.setVisibility(View.GONE);
        confirmPasswordInput.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        loginLink.setVisibility(View.VISIBLE);
    }
}