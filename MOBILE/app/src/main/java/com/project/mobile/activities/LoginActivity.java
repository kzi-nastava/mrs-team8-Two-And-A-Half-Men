package com.project.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.AdminActivity;
import com.project.mobile.DTO.UserLoginRequest;
import com.project.mobile.DriverActivity;
import com.project.mobile.ForgetPasswordActivity;
import com.project.mobile.MainActivity;
import com.project.mobile.R;
import com.project.mobile.viewModels.AuthModel;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText username;
    private EditText password;
    private CheckBox rememberMe;
    private Button loginButton;
    private TextView forgotPassword;
    private TextView signUp;
    private AuthModel authModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.authModel = new ViewModelProvider(this).get(AuthModel.class);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        rememberMe = findViewById(R.id.remember_me);
        loginButton = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_password);
        signUp = findViewById(R.id.sign_up);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        loginButton.setOnClickListener(v -> {
            if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                if(username.getText().toString().isEmpty()){
                    username.setError("Username is required");
                }
                if(password.getText().toString().isEmpty()){
                    password.setError("Password is required");
                }
                return;
            }
            UserLoginRequest request = new UserLoginRequest(username.getText().toString(), password.getText().toString());
            this.authModel.loginUser(request);

            authModel.loginResultLiveData.observe(this, loginResult -> {
                if(loginResult.isSuccess()) {
                    if (loginResult.getRole().equals("ADMIN")) {
                        Intent intent = new Intent(this, AdminActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else if (loginResult.getRole().equals("DRIVER")) {
                        Intent intent = new Intent(this, DriverActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else if(loginResult.getRole().equals("CUSTOMER")) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        finish();
                    }
                    }
                    else {
                    String errorMessage = loginResult.getErrorMessage() != null ? loginResult.getErrorMessage() : "Login failed. Please try again.";
                    if(errorMessage.equals("Invalid username or password.")){
                        password.setError(errorMessage);
                    } else {
                        username.setError(errorMessage);
                    }
                }
            });


        });
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgetPasswordActivity.class);
            startActivity(intent);
        });
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}