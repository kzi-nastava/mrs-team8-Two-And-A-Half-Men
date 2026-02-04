package com.project.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.project.mobile.DTO.RegisterDto;
import com.project.mobile.databinding.ActivityRegisterBinding;
import com.project.mobile.viewModels.AuthModel;


public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    int currentStep = 1;
    String firstName, lastName, phoneNumber, address, email, password, confirmPassword;
    private AuthModel authModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.authModel = new ViewModelProvider(this).get(AuthModel.class);
        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("currentStep", 1);
            firstName = savedInstanceState.getString("firstName", "");
            lastName = savedInstanceState.getString("lastName", "");
            phoneNumber = savedInstanceState.getString("phoneNumber", "");
            address = savedInstanceState.getString("address", "");
            email = savedInstanceState.getString("email", "");
            password = savedInstanceState.getString("password", "");
            confirmPassword = savedInstanceState.getString("confirmPassword", "");
        }
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showForm(currentStep);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.toolbar.setNavigationOnClickListener(v -> {
            finish(); // Close this activity and go back
        });
        binding.nextRegisterButton.setOnClickListener(v -> {
            if(ValidateRegistrationInputs(currentStep)){
                currentStep++;
                showForm(currentStep);
            }});
        binding.registerButton.setOnClickListener(v -> {;
            if(ValidateRegistrationInputs(currentStep)){
                RegisterDto registerDto = new RegisterDto(firstName, lastName, email, address, phoneNumber, password);

                 authModel.RegisterUser(registerDto).thenAccept(success -> {
                    runOnUiThread(() -> {
                        Toast.makeText(this, success.getMessage(), Toast.LENGTH_SHORT).show();
                        if(success.isSuccess()) {
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    });
                    }).exceptionally(e -> {
                        return null;
                    });

            }});
        binding.backButton.setOnClickListener(v -> {
            currentStep--;
            showForm(currentStep);
        });
        binding.signIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentStep > 1) {
                    currentStep--;
                    showForm(currentStep);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });


    }


    private void showForm(int step)
    {
        if(step == 1)
        {
            binding.inputField1.setVisibility(android.view.View.VISIBLE);
            binding.inputField2.setVisibility(android.view.View.GONE);
        }
        else if(step == 2)
        {
            binding.inputField1.setVisibility(android.view.View.GONE);
            binding.inputField2.setVisibility(android.view.View.VISIBLE);
        }
    }
    private boolean ValidateRegistrationInputs(int step){
        if(step == 1)
        {
            String name = binding.firstName.getText().toString().trim();
            String last_name = binding.lastName.getText().toString().trim();
            String Phone_number = binding.phoneNumber.getText().toString().trim();
            String address_filed = binding.Address.getText().toString().trim();
            if(name.isEmpty()){
                binding.firstName.setError("First name is required");
                binding.firstName.requestFocus();
                return false;
            }
            if(last_name.isEmpty()){
                binding.lastName.setError("Last name is required");
                binding.lastName.requestFocus();
                return false;
            }
            if(Phone_number.isEmpty()){
                binding.phoneNumber.setError("Phone number is required");
                binding.phoneNumber.requestFocus();
                return false;
            }
            if (!Phone_number.matches("^[0-9]{10,15}$")) {
                binding.phoneNumber.setError("Please enter a valid phone number");
                binding.phoneNumber.requestFocus();
                return false;
            }
            if(address_filed.isEmpty()) {
                binding.Address.setError("Address is required");
                binding.Address.requestFocus();
                return false;
            }
            firstName = name;
            lastName = last_name;
            phoneNumber = Phone_number;
            address = address_filed;
            return true;
        }
        else if(step == 2)
        {
            String email_filed = binding.email.getText().toString().trim();
            String password_filed = binding.password.getText().toString().trim();
            String confirm_password_filed = binding.confirmPassword.getText().toString().trim();
            if(email_filed.isEmpty()){
                binding.email.setError("Email is required");
                binding.email.requestFocus();
                return false;
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email_filed).matches()){
                binding.email.setError("Please enter a valid email");
                binding.email.requestFocus();
                return false;
            }
            if(password_filed.isEmpty()){
                binding.password.setError("Password is required");
                binding.password.requestFocus();
                return false;
            }
            if(confirm_password_filed.isEmpty()){
                binding.confirmPassword.setError("Please confirm your password");
                binding.confirmPassword.requestFocus();
                return false;
            }
            if(!password_filed.equals(confirm_password_filed)){
                binding.confirmPassword.setError("Passwords do not match");
                binding.confirmPassword.requestFocus();
                return false;
            }
            if(password_filed.length() < 6){
                binding.password.setError("Password should be at least 6 characters long");
                binding.password.requestFocus();
                return false;
            }
            email = email_filed;
            password = password_filed;
            confirmPassword = confirm_password_filed;
            return true;
        }
        return false;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentStep", currentStep);
        outState.putString("firstName", firstName);
        outState.putString("lastName", lastName);
        outState.putString("phoneNumber", phoneNumber);
        outState.putString("address", address);
        outState.putString("email", email);
        outState.putString("password", password);
        outState.putString("confirmPassword", confirmPassword);
    }
}