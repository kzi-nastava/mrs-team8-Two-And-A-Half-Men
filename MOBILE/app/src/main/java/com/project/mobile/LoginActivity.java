package com.project.mobile;

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

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText username;
    private EditText password;
    private CheckBox rememberMe;
    private Button loginButton;
    private TextView forgotPassword;
    private TextView signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            finish(); // Close this activity and go back
        });
        loginButton.setOnClickListener(v -> {
            // Handle login logic here
        });
        forgotPassword.setOnClickListener(v -> {
            // Handle forgot password logic here
        });
        signUp.setOnClickListener(v -> {

        });

    }
}