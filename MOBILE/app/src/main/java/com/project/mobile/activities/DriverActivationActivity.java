package com.project.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.project.mobile.DTO.Auth.DriverActivationRequestDTO;
import com.project.mobile.DTO.MessageResponse;
import com.project.mobile.R;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.service.AuthService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DriverActivationActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private Button btnActivate;
    private String accessToken;
    private AuthService authService;
    private boolean isActivating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_activation);

        // Initialize service
        Retrofit retrofit = getRetrofitInstance(); // TODO: Implement this
        authService = retrofit.create(AuthService.class);

        initViews();
        extractTokenFromIntent();
        setupButton();
    }

    private void initViews() {
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnActivate = findViewById(R.id.btnActivate);
    }

    private void extractTokenFromIntent() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            // Handle deep link: http://localhost:4200/driver-activation?token=xyz
            accessToken = data.getQueryParameter("token");
        }

        if (accessToken == null || accessToken.isEmpty()) {
            Toast.makeText(this, R.string.error_no_token, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupButton() {
        btnActivate.setOnClickListener(v -> activateAccount());
    }

    private void activateAccount() {
        if (isActivating) return;

        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.error_password_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, R.string.error_passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 8) {
            Toast.makeText(this, R.string.error_password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        isActivating = true;
        btnActivate.setEnabled(false);

        DriverActivationRequestDTO request = new DriverActivationRequestDTO(accessToken, newPassword);

        authService.activateDriverAccount(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                isActivating = false;
                btnActivate.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    Toast.makeText(DriverActivationActivity.this, message, Toast.LENGTH_LONG).show();

                    // Navigate to login
                    navigateToLogin();
                } else {
                    Toast.makeText(DriverActivationActivity.this, 
                        R.string.error_activation_failed, 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                isActivating = false;
                btnActivate.setEnabled(true);
                Toast.makeText(DriverActivationActivity.this,
                    getString(R.string.error_activation_failed) + " " + t.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private Retrofit getRetrofitInstance() {
        // Return your Retrofit instance here
        return RetrofitClient.retrofit;
    }
}
